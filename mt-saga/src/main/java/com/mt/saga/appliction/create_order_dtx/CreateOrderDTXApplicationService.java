package com.mt.saga.appliction.create_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.create_order_dtx.command.*;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelCreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.create_order_dtx.event.ClearCartEvent;
import com.mt.saga.domain.model.create_order_dtx.event.DecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.create_order_dtx.event.GeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.create_order_dtx.event.SaveNewOrderEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateCreateOrderDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CreateOrderDTXApplicationService {

    public static final String APP_CREATE_ORDER_DTX = "CreateOrderDTX";
    public static final String APP_CLEAR_CART = "clearCart";
    public static final String APP_DECREASE_ORDER_STORAGE = "decreaseOrderStorage";
    public static final String APP_GENERATE_PAYMENT_QR_LINK = "generatePaymentQRLink";
    public static final String APP_SAVE_NEW_ORDER = "saveNewOrder";
    public static final String APP_CREATE_ORDER_DTX1 = "createOrderDtx";
    public static final String ORDER_ID = "ORDER_ID";
    public static final String CHANGE_ID = "CHANGE_ID";
    public static final String COMMAND = "COMMAND";

    @SubscribeForEvent
    @Transactional
    public void createCreateOrderTask(CreateCreateOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            CommonOrderCommand command = event.getCommand();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                LocalTx localTx1 = new LocalTx(APP_GENERATE_PAYMENT_QR_LINK, AppConstant.GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT);
                LocalTx localTx2 = new LocalTx(APP_DECREASE_ORDER_STORAGE, AppConstant.DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT);
                LocalTx localTx3 = new LocalTx(APP_CLEAR_CART, AppConstant.CLEAR_CART_FOR_CREATE_EVENT);
                LocalTx localTx4 = new LocalTx(APP_SAVE_NEW_ORDER, AppConstant.SAVE_NEW_ORDER_FOR_CREATE_EVENT);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                localTxes.add(localTx2);
                localTxes.add(localTx3);
                localTxes.add(localTx4);
                DistributedTx distributedTx = new DistributedTx(localTxes, APP_CREATE_ORDER_DTX1, event.getCommand().getTxId(), event.getCommand().getOrderId());
                distributedTx.startLocalTx(APP_GENERATE_PAYMENT_QR_LINK);
                distributedTx.startLocalTx(APP_DECREASE_ORDER_STORAGE);
                distributedTx.startLocalTx(APP_CLEAR_CART);
                distributedTx.updateParams(ORDER_ID, event.getCommand().getOrderId());
                distributedTx.updateParams(CHANGE_ID, event.getCommand().getTxId());
                distributedTx.updateParams(COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                DomainEventPublisher.instance().publish(new GeneratePaymentQRLinkEvent(event.getCommand().getOrderId(), event.getCommand().getTxId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new DecreaseOrderStorageForCreateEvent(event.getCommand().getOrderId(), event.getCommand().getTxId(), distributedTx.getId(), command));
                DomainEventPublisher.instance().publish(new ClearCartEvent(command, event.getCommand().getTxId(), distributedTx.getId()));
                DomainRegistry.getDistributedTxRepository().store(distributedTx);
            }, command.getOrderId());
            return null;
        }, APP_CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(ClearCartReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(command);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_CLEAR_CART, replyEvent);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, APP_CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(DecreaseOrderStorageForCreateReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(command);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_DECREASE_ORDER_STORAGE, replyEvent);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, APP_CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(GeneratePaymentQRLinkReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(command);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_GENERATE_PAYMENT_QR_LINK, replyEvent);
                SaveNewOrderEvent event = new SaveNewOrderEvent(command, CommonDomainRegistry.getCustomObjectSerializer().deserialize(e.getParameters().get(COMMAND),CommonOrderCommand.class), e.getId(), (String) e.getParameters().get(CHANGE_ID));
                e.startLocalTx(APP_SAVE_NEW_ORDER);
                DomainEventPublisher.instance().publish(event);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, APP_CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(SaveNewOrderReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(command);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_SAVE_NEW_ORDER, replyEvent);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, APP_CREATE_ORDER_DTX);
    }

    public SumPagedRep<DistributedTx> query(String queryParam, String pageParam, String skipCount) {
        DistributedTxQuery var0 = new DistributedTxQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getDistributedTxRepository().query(var0);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(dtxId);
            byId.ifPresent(e -> e.cancel(AppConstant.CREATE_ORDER_DTX_FAILED_EVENT));
            return null;
        }, "SystemCancelCreateOrderDtx");
    }

    public Optional<DistributedTx> query(long id) {
        return DomainRegistry.getDistributedTxRepository().getById(id);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(CancelCreateOrderDTXSuccessEvent deserialize) {
        Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(e -> {
            if (e.getStatus().equals(DTXStatus.STARTED)) {
                SumPagedRep<StoredEvent> relatedEvents = CommonDomainRegistry.getEventRepository().query(
                        new StoredEventQuery("domainId:" + e.getId(),
                                PageConfig.defaultConfig().getRawValue()
                                , QueryConfig.skipCount().value()));

                relatedEvents.getData().forEach(event -> {
                    if (e.isLocalTxStarted(APP_DECREASE_ORDER_STORAGE)) {
                        if (DecreaseOrderStorageForCreateEvent.name.equals(event.getName())) {
                            CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                        }
                    }
                    if (e.isLocalTxStarted(APP_GENERATE_PAYMENT_QR_LINK)) {
                        if (GeneratePaymentQRLinkEvent.name.equals(event.getName())) {
                            CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                        }
                    }
                    if (e.isLocalTxStarted(APP_SAVE_NEW_ORDER)) {
                        if (SaveNewOrderEvent.name.equals(event.getName())) {
                            CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                        }
                    }
                    if (e.isLocalTxStarted(APP_CLEAR_CART)) {
                        if (ClearCartEvent.name.equals(event.getName())) {
                            CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                        }
                    }
                });
            }
        });
    }

    @Transactional
    @SubscribeForEvent
    public void handle(ClearCartFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }

    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForCreateFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
