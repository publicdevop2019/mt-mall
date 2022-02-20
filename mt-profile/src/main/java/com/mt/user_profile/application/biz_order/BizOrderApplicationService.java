package com.mt.user_profile.application.biz_order;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.distributed_lock.SagaDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.exception.AggregateOutdatedException;
import com.mt.user_profile.application.ApplicationServiceRegistry;
import com.mt.user_profile.application.biz_order.command.*;
import com.mt.user_profile.domain.DomainRegistry;
import com.mt.user_profile.domain.biz_order.BizOrderId;
import com.mt.user_profile.domain.biz_order.BizOrderQuery;
import com.mt.user_profile.domain.biz_order.BizOrderSummary;
import com.mt.user_profile.domain.biz_order.UserThreadLocal;
import com.mt.user_profile.domain.biz_order.event.*;
import com.mt.user_profile.domain.cart.event.ClearCartForCreateFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mt.common.domain.model.constant.AppInfo.EventName.MT2_ORDER_UPDATE_FAILED;

@Slf4j
@Service
public class BizOrderApplicationService {
    private static final String AGGREGATE_NAME = "order";
    private final CommandGateway commandGateway;
    @Value("${spring.application.name}")
    private String appName;

    public BizOrderApplicationService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @SubscribeForEvent
    @Transactional
    public String handleUserPlaceOrderAction(CustomerPlaceBizOrderCommand command, String changeId) {
        return DomainRegistry.getBizOrderService().handlePlaceOrderAction(command, changeId).getDomainId();
    }

    @SubscribeForEvent
    @Transactional
    public void updateAddress(CustomerUpdateBizOrderAddressCommand command, String id, String changeId) {
        DomainRegistry.getBizOrderService().updateAddress(id, changeId,command);
    }

    @SubscribeForEvent
    @Transactional
    public void confirmPayment(String id, String changeId) {
        DomainRegistry.getBizOrderService().confirmPayment(id, changeId);
    }

    @Transactional
    @SubscribeForEvent
    public void reserveOrder(String id, String changeId) {
        DomainRegistry.getBizOrderService().reserveOrder(id, changeId);
    }

    @SubscribeForEvent
    @Transactional
    public CompletableFuture<ResponseEntity<Object>> deleteOrder(String id, String changeId,Integer version) {
        InternalAdminDeleteOrderEvent updateBizOrderCommand = new InternalAdminDeleteOrderEvent();
        updateBizOrderCommand.setChangeId(changeId);
        updateBizOrderCommand.setVersion(version);
        updateBizOrderCommand.setUserId(UserThreadLocal.get());
        updateBizOrderCommand.setOrderId(new BizOrderId(id));
        return commandGateway.send(updateBizOrderCommand).thenApply(e -> ResponseEntity.ok().build()).exceptionally(e -> {
            Throwable throwable = unwrapAxonException(e.getCause());
            if (throwable instanceof AggregateOutdatedException)
                return ResponseEntity.badRequest().build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
    }
    @SubscribeForEvent
    @Transactional
    public void deleteOrderByUser(String id, String changeId) {
        DomainRegistry.getBizOrderService().userCancelOrder(id, changeId);
    }

    public SumPagedRep<BizOrderSummary> ordersForUser(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getBizOderSummaryRepository().ordersOfQuery(new BizOrderQuery(queryParam, pageParam, skipCount, false));
    }

    public SumPagedRep<BizOrderSummary> orders(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getBizOderSummaryRepository().ordersOfQuery(new BizOrderQuery(queryParam, pageParam, skipCount, true));
    }

    public Optional<BizOrderSummary> orderOfId(String id) {
        return DomainRegistry.getBizOderSummaryRepository().ordersOfQuery(new BizOrderQuery(new BizOrderId(id), true)).findFirst();
    }

    public Optional<BizOrderSummary> orderOfIdForUser(String id) {
        return DomainRegistry.getBizOderSummaryRepository().ordersOfQuery(new BizOrderQuery(new BizOrderId(id), false)).findFirst();
    }

    @SubscribeForEvent
    @Transactional
    public void releaseExpiredOrder() {
        DomainRegistry.getBizOrderService().releaseExpiredOrder();
    }

    @SubscribeForEvent
    @Transactional
    public void concludeOrder() {
        DomainRegistry.getBizOrderService().concludeOrder();
    }

    @SubscribeForEvent
    @Transactional
    public void resubmitOrder() {
        DomainRegistry.getBizOrderService().resubmitOrder();
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(InternalCreateNewOrderCommand command) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                commandGateway.send(command).get();
                log.debug("send create order success event");
            } catch (InterruptedException | ExecutionException e) {
                log.error("error during create order", e);
                notifyAdmin(e,command.getChangeId(),command.getOrderId().getDomainId());
                createCancelCreateDtx(command.getTaskId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (command1)->{
            DomainEventPublisher.instance().publish(new SaveNewOrderForCreateReplyEvent(command.getOrderId(), command1.isEmptyOpt(), command.getTaskId()));
            return null;
        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(UpdateOrderForRecycleCommand command) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("error during update order ", e);
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                createCancelRecycleDtx(command.getTaskId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (command1)->{
            DomainEventPublisher.instance().publish(new UpdateOrderForRecycleReplyEvent(command1.isEmptyOpt(), command.getTaskId()));
            return null;
        }, AGGREGATE_NAME);
    }


    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(UpdateOrderForReserveCommand command) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("error during update order ", e);
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                createCancelReserveDtx(command.getTaskId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (command1)->{
            DomainEventPublisher.instance().publish(new UpdateOrderForReserveReplyEvent(command1.isEmptyOpt(), command.getTaskId()));
            return null;
        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(UpdateOrderPaymentSuccessCommand command) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("error during update order ", e);
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                createCancelPaymentSuccessDtx(command.getTaskId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (command1)->{
            DomainEventPublisher.instance().publish(new UpdateOrderPaymentSuccessReplyEvent(command1.isEmptyOpt(), command.getTaskId()));
            return null;
        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(UpdateOrderForConcludeCommand command) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("error during update order ", e);
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                createCancelConcludeDtx(command.getTaskId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (command1)->{
            DomainEventPublisher.instance().publish(new UpdateOrderForConcludeReplyEvent(command1.isEmptyOpt(), command.getTaskId()));
            return null;
        },AGGREGATE_NAME);
    }


    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(InternalCancelNewOrderEvent command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                commandGateway.send(command).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("error during update order ", e);
                notifyAdmin(e,command.getChangeId(),command.getOrderId().getDomainId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new CancelSaveNewOrderForCreateReplyEvent(ignore.isEmptyOpt(), command.getTaskId()));
            return  null;
            },AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(CancelUpdateOrderForReserveCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                throw new IllegalArgumentException("error during event sourcing");
            }

            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForReserveReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;

        },AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(CancelUpdateOrderForRecycleCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                throw new IllegalArgumentException("error during event sourcing");
            }

            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForRecycleReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;

        },AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(CancelUpdateOrderForConcludeCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForConcludeReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;

        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(CancelUpdateOrderPaymentSuccessCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new CancelUpdateOrderPaymentSuccessReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;

        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(UpdateOrderForUpdateAddressCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                CustomerUpdateBizOrderAddressCommand customerUpdateBizOrderAddressCommand = new CustomerUpdateBizOrderAddressCommand(command);
                commandGateway.send(customerUpdateBizOrderAddressCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                createCancelAddressUpdateDtx(command.getTaskId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new UpdateOrderAddressForUpdateAddressReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(CancelUpdateOrderForUpdateOrderAddressCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                CustomerUpdateBizOrderAddressCommand internalUpdateBizOrderCommand = new CustomerUpdateBizOrderAddressCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new CancelUpdateOrderAddressForUpdateAddressReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;

        }, AGGREGATE_NAME);
    }
    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(UpdateOrderForInvalidCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                createCancelInvalidDtx(command.getTaskId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new UpdateOrderForInvalidReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;

        }, AGGREGATE_NAME);
    }
    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(CancelUpdateOrderForInvalidCommand command) {
        log.debug("handling {}", command);
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(command.getChangeId(), (change) -> {
            try {
                InternalUpdateBizOrderCommand internalUpdateBizOrderCommand = new InternalUpdateBizOrderCommand(command);
                commandGateway.send(internalUpdateBizOrderCommand).get();
            } catch (InterruptedException | ExecutionException e) {
                notifyAdmin(e,command.getChangeId(),command.getOrderId());
                throw new IllegalArgumentException("error during event sourcing");
            }
            return null;
        }, (ignore)->{
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForInvalidReplyCommand(command.getTaskId(), ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }
    private Throwable unwrapAxonException(Throwable throwable) {
        if (throwable instanceof CommandExecutionException) {
            CommandExecutionException cee = (CommandExecutionException) throwable;
            if (cee.getDetails().isPresent()) {
                Object o = cee.getDetails().get();
                return (RuntimeException) o;
            } else {
                log.error("unknown exception " + throwable);
                return throwable;
            }
        } else {
            log.error("unknown exception " + throwable);
            return throwable;
        }
    }
    private void notifyAdmin(Exception ex, String changeId,String orderId) {
        //directly publish msg to stream
        MallNotificationEvent event = MallNotificationEvent.create(MT2_ORDER_UPDATE_FAILED);
        event.setChangeId(changeId);
        event.setOrderId(orderId);
        event.addDetail(AppInfo.MISC.STACK_TRACE,
                ex.getLocalizedMessage());
        StoredEvent storedEvent = new StoredEvent(event);
        storedEvent.setIdExplicitly(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        CommonDomainRegistry.getEventStreamService().next(appName, event.isInternal(), event.getTopic(), storedEvent);
    }
    private void createCancelCreateDtx(long taskId) {
        StoredEvent storedEvent = new StoredEvent(new OrderUpdateForCreateFailed(taskId));
        publishCancelEvent(storedEvent);
    }
    private void createCancelReserveDtx(long taskId) {
        StoredEvent storedEvent = new StoredEvent(new OrderUpdateForReserveFailed(taskId));
        publishCancelEvent(storedEvent);
    }
    private void createCancelRecycleDtx(long taskId) {
        StoredEvent storedEvent = new StoredEvent(new OrderUpdateForRecycleFailed(taskId));
        publishCancelEvent(storedEvent);
    }
    private void createCancelPaymentSuccessDtx(long taskId) {
        StoredEvent storedEvent = new StoredEvent(new OrderUpdateForPaymentSuccessFailed(taskId));
        publishCancelEvent(storedEvent);
    }
    private void createCancelConcludeDtx(long taskId) {
        StoredEvent storedEvent = new StoredEvent(new OrderUpdateForConcludeFailed(taskId));
        publishCancelEvent(storedEvent);
    }
    private void createCancelAddressUpdateDtx(long taskId) {
        StoredEvent storedEvent = new StoredEvent(new OrderUpdateForAddressUpdateFailed(taskId));
        publishCancelEvent(storedEvent);
    }
    private void createCancelInvalidDtx(long taskId) {
        StoredEvent storedEvent = new StoredEvent(new OrderUpdateForInvalidFailed(taskId));
        publishCancelEvent(storedEvent);
    }
    private void publishCancelEvent(StoredEvent event){
        event.setIdExplicitly(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        CommonDomainRegistry.getEventStreamService().next(event);
    }
}
