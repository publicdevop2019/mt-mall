package com.mt.saga.appliction.create_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.create_order_dtx.command.*;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelCreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTXQuery;
import com.mt.saga.domain.model.create_order_dtx.event.ClearCartEvent;
import com.mt.saga.domain.model.create_order_dtx.event.CreateOrderDTXFailedEvent;
import com.mt.saga.domain.model.create_order_dtx.event.DecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.create_order_dtx.event.GeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateCreateOrderDTXEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class CreateOrderDTXApplicationService {

    public static final String CREATE_ORDER_DTX = "CreateOrderDTX";

    @SubscribeForEvent
    @Transactional
    public void createCreateOrderTask(CreateCreateOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            CommonOrderCommand command = event.getCommand();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored)->{
                CreateOrderDTX dtx = new CreateOrderDTX(event);
                DomainEventPublisher.instance().publish(new GeneratePaymentQRLinkEvent(dtx));
                DomainEventPublisher.instance().publish(new DecreaseOrderStorageForCreateEvent(dtx, command));
                DomainEventPublisher.instance().publish(new ClearCartEvent(command, dtx));
                dtx.markAsStarted();
                DomainRegistry.getCreateOrderDTXRepository().createOrUpdate(dtx);
            },command.getOrderId());
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(ClearCartReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<CreateOrderDTX> byIdLocked = DomainRegistry.getCreateOrderDTXRepository().getById(command.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(command);
                DomainRegistry.getCreateOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(DecreaseOrderStorageForCreateReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<CreateOrderDTX> byIdLocked = DomainRegistry.getCreateOrderDTXRepository().getById(command.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(command);
                DomainRegistry.getCreateOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(GeneratePaymentQRLinkReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<CreateOrderDTX> byIdLocked = DomainRegistry.getCreateOrderDTXRepository().getById(command.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(command);
                DomainRegistry.getCreateOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(SaveNewOrderReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<CreateOrderDTX> byIdLocked = DomainRegistry.getCreateOrderDTXRepository().getById(command.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(command);
                DomainRegistry.getCreateOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }

    public SumPagedRep<CreateOrderDTX> query(String queryParam, String pageParam, String skipCount) {
        CreateOrderDTXQuery var0 = new CreateOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCreateOrderDTXRepository().query(var0);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<CreateOrderDTX> byId = DomainRegistry.getCreateOrderDTXRepository().getById(dtxId);
            byId.ifPresent(CreateOrderDTX::cancel);
            return null;
        }, "SystemCancelCreateOrderDtx");
    }

    public Optional<CreateOrderDTX> query(long id) {
        return DomainRegistry.getCreateOrderDTXRepository().getById(id);
    }

    public void handle(CancelCreateOrderDTXSuccessEvent deserialize) {
        Optional<CreateOrderDTX> byId = DomainRegistry.getCreateOrderDTXRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(CreateOrderDTX::retryStartedLtx);
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
