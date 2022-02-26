package com.mt.shop.application.cart;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.distributed_lock.SagaDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.cart.command.*;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.biz_order.UserThreadLocal;
import com.mt.shop.domain.model.cart.BizCart;
import com.mt.shop.domain.model.cart.CartItemId;
import com.mt.shop.domain.model.cart.CartQuery;
import com.mt.shop.domain.model.cart.event.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.mt.common.domain.model.constant.AppInfo.EventName.MT2_CART_UPDATE_FAILED;

@Slf4j
@Service
public class CartApplicationService {
    public static final String AGGREGATE_NAME = "BizCart";
    public static final String ID = "id";
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Value("${spring.application.name}")
    private String appName;

    public SumPagedRep<BizCart> getAllInCart() {
        return DomainRegistry.getBizCartRepository().cartItemOfQuery(new CartQuery());
    }

    @SubscribeForEvent
    public String addToCart(UserCreateBizCartItemCommand command, String changeId) {
        log.debug("before adding cart for user {}, acquire lock", UserThreadLocal.get());
        RLock lock = redissonClient.getLock(UserThreadLocal.get() + "_cart");
        lock.lock(5, TimeUnit.SECONDS);
        log.debug("lock acquired");
        try {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            String bizCart = template.execute(new TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    return ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId,
                            (change) -> {
                                CartItemId cartItemId = new CartItemId();
                                BizCart bizCartItem = BizCart.create(cartItemId, command);
                                DomainRegistry.getBizCartRepository().add(bizCartItem);
                                change.setReturnValue(cartItemId.getDomainId());
                                return cartItemId.getDomainId();
                            }, AGGREGATE_NAME
                    );
                }
            });
            return bizCart;
        } finally {
            log.debug("release lock");
            lock.unlock();
        }
    }

    @SubscribeForEvent
    @Transactional
    public void removeFromCart(String id, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (change) -> {
            CartItemId cartItemId = new CartItemId(id);
            SumPagedRep<BizCart> var0 = DomainRegistry.getBizCartRepository().cartItemOfQuery(new CartQuery(cartItemId));
            var0.findFirst().ifPresent(e -> {
                DomainRegistry.getBizCartRepository().remove(e);
            });
            return null;
        }, AGGREGATE_NAME);
    }


    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void handle(ClearCartForCreateEvent event) {
        CartQuery cartQuery = new CartQuery(event);
        String changeId = event.getChangeId();
        long taskId = event.getTaskId();
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(changeId, (change) -> {
            try {
                Validator.notEmpty(cartQuery.getCartItemIds());
            } catch (Exception ex) {
                createCancelDtx(taskId);
                throw ex;
            }
            Set<BizCart> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getBizCartRepository().cartItemOfQuery((CartQuery) query), cartQuery);
            try {
                Validator.notEmpty(allByQuery);
                Validator.sizeEqualTo(cartQuery.getCartItemIds(), allByQuery, "unable to find all cart items");
            } catch (Exception ex) {
                createCancelDtx(taskId);
                throw ex;
            }
            log.debug("num of cart needs to be removed is {}", allByQuery.size());
            DomainRegistry.getBizCartRepository().removeAll(allByQuery);
            return null;
        }, (command) -> {
            DomainEventPublisher.instance().publish(new ClearCartForCreateReplyEvent(taskId, command.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }

    private void createCancelDtx(long taskId) {
        ClearCartForCreateFailedEvent event = new ClearCartForCreateFailedEvent(taskId);
        StoredEvent storedEvent = new StoredEvent(new ClearCartForCreateFailedEvent(taskId));
        storedEvent.setIdExplicitly(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        CommonDomainRegistry.getEventStreamService().next(appName, event.isInternal(), event.getTopic(), storedEvent);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void cancelClearCart(CancelClearCartForCreateCommand event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(event.getChangeId(), (change) -> {
            CartQuery cartQuery = new CartQuery(event);
            try {

                Validator.notEmpty(cartQuery.getCartItemIds());
            } catch (Exception ex) {
                notifyAdmin("trying to cancel empty cart", event.getChangeId(), event.getDomainId().getDomainId());
                throw ex;
            }
            Set<BizCart> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getBizCartRepository().removedCartItemOfQuery((CartQuery) query), cartQuery);
            try {

                Validator.notEmpty(allByQuery);
            } catch (Exception ex) {
                notifyAdmin("could not find any item for cancel", event.getChangeId(), event.getDomainId().getDomainId());
                throw ex;
            }
            String error = "unable to find all cart items";
            try {

                Validator.sizeEqualTo(cartQuery.getCartItemIds(), allByQuery, error);
            } catch (Exception ex) {

                notifyAdmin(error, event.getChangeId(), event.getDomainId().getDomainId());
            }

            log.debug("num of cart needs to be restored is {}", allByQuery.size());
            boolean b = allByQuery.stream().anyMatch(e -> !e.getVersion().equals(event.getIdVersionMap().get(e.getCartItemId().getDomainId())));
            String error2 = "invalid version match";
            if (b) {
                notifyAdmin(error2, event.getChangeId(), event.getDomainId().getDomainId());
                throw new IllegalArgumentException(error2);
            } else {
                DomainRegistry.getBizCartRepository().restoreAll(allByQuery);
            }
            return null;
        }, (command) -> {
            DomainEventPublisher.instance().publish(new CancelClearCartForCreateReplyEvent(event.getTaskId(), command.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);

    }

    @SubscribeForEvent
    @Transactional
    public void restoreRemoved(String queryString, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (change) -> {
            Set<BizCart> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getBizCartRepository().internalCartItemOfQuery((CartQuery) query), new CartQuery(queryString));
            Validator.notEmpty(allByQuery);
            String id = QueryUtility.parseQuery(queryString, ID).get(ID);
            int length = id.split("\\.").length;
            if (length != allByQuery.size()) {
                String var0 = "unable to find all cart items";
                throw new IllegalArgumentException(var0);
            }
            log.debug("num of cart needs to be restored is {}", allByQuery.size());
            if (allByQuery.size() != 0) {
                allByQuery.forEach(e -> {
                    e.setDeleted(false);
                    DomainRegistry.getBizCartRepository().add(e);
                });
            }
            return null;
        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void handle(RestoreCartForInvalidEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(event.getChangeId(), (change) -> {
            CartQuery cartQuery = new CartQuery(event);
            Validator.notEmpty(cartQuery.getCartItemIds());
            Set<BizCart> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getBizCartRepository().removedCartItemOfQuery((CartQuery) query), cartQuery);
            Validator.notEmpty(allByQuery);
            String error = "unable to find all cart items";
            Validator.sizeEqualTo(cartQuery.getCartItemIds(), allByQuery, error);
            log.debug("num of cart needs to be restored for invalid is {}", allByQuery.size());
            //increase version by one since create success
            boolean b = allByQuery.stream().anyMatch(e -> !e.getVersion().equals((event.getIdVersionMap().get(e.getCartItemId().getDomainId()) + 1)));
            String error2 = "invalid version match";
            if (b) {
                throw new IllegalArgumentException(error2);
            } else {
                DomainRegistry.getBizCartRepository().restoreAll(allByQuery);
            }
            return null;
        }, (command) -> {
            DomainEventPublisher.instance().publish(new RestoreCartForInvalidReplyEvent(event.getTaskId(), command.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void handle(CancelRestoreCartForInvalidEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(event.getChangeId(), (change) -> {
            CartQuery cartQuery = new CartQuery(event);
            try {
                Validator.notEmpty(cartQuery.getCartItemIds());
            } catch (Exception ex) {
                notifyAdmin("trying to cancel restore nothing", event.getChangeId(), event.getDomainId().getDomainId());
                throw ex;
            }
            Set<BizCart> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getBizCartRepository().cartItemOfQuery((CartQuery) query), cartQuery);
            try {
                Validator.notEmpty(allByQuery);
            } catch (Exception ex) {
                notifyAdmin("could not find any item to restore for cancel invalid", event.getChangeId(), event.getDomainId().getDomainId());
                throw ex;
            }
            String error = "unable to find all cart items";
            try {
                Validator.sizeEqualTo(cartQuery.getCartItemIds(), allByQuery, error);
            } catch (Exception ex) {
                notifyAdmin(error, event.getChangeId(), event.getDomainId().getDomainId());
            }

            log.debug("num of cart needs to be cancel restored for invalid is {}", allByQuery.size());
            //increase version by one since create success
            boolean b = allByQuery.stream().anyMatch(e -> !e.getVersion().equals((event.getIdVersionMap().get(e.getCartItemId().getDomainId()) + 2)));
            String error2 = "invalid version match";
            if (b) {
                notifyAdmin(error2, event.getChangeId(), event.getDomainId().getDomainId());
                throw new IllegalArgumentException(error2);
            } else {
                DomainRegistry.getBizCartRepository().removeAll(allByQuery);
            }
            return null;
        }, (command) -> {
            DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidReplyEvent(event.getTaskId(), command.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }

    private void notifyAdmin(String message, String changeId, String orderId) {
        //directly publish msg to stream
        MallNotificationEvent event = MallNotificationEvent.create(MT2_CART_UPDATE_FAILED);
        event.setChangeId(changeId);
        event.setOrderId(orderId);
        event.addDetail(AppInfo.MISC.MESSAGE,
                message);
        StoredEvent storedEvent = new StoredEvent(event);
        storedEvent.setIdExplicitly(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        CommonDomainRegistry.getEventStreamService().next(appName, event.isInternal(), event.getTopic(), storedEvent);
    }

}
