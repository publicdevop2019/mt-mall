package com.mt.saga.appliction.create_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.create_order_dtx.command.*;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.create_order_dtx.event.ClearCartEvent;
import com.mt.saga.domain.model.create_order_dtx.event.DecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.create_order_dtx.event.GeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.create_order_dtx.event.SaveNewOrderEvent;
import com.mt.saga.domain.model.distributed_tx.DTXSuccessEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
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

    public static final String ORDER_ID = "ORDER_ID";
    public static final String CHANGE_ID = "CHANGE_ID";
    public static final String COMMAND = "COMMAND";
    private static final String CREATE_ORDER_DTX = "CreateOrderDtx";

    @SubscribeForEvent
    @Transactional
    public void createCreateOrderTask(CreateCreateOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            CommonOrderCommand command = event.getCommand();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                LocalTx localTx1 = new LocalTx(GeneratePaymentQRLinkEvent.name, GeneratePaymentQRLinkEvent.name);
                LocalTx localTx2 = new LocalTx(DecreaseOrderStorageForCreateEvent.name, DecreaseOrderStorageForCreateEvent.name);
                LocalTx localTx3 = new LocalTx(ClearCartEvent.name, ClearCartEvent.name);
                LocalTx localTx4 = new LocalTx(SaveNewOrderEvent.name, SaveNewOrderEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                localTxes.add(localTx2);
                localTxes.add(localTx3);
                localTxes.add(localTx4);
                DistributedTx distributedTx = new DistributedTx(localTxes, "createOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId());
                distributedTx.startLocalTx(GeneratePaymentQRLinkEvent.name);
                distributedTx.startLocalTx(DecreaseOrderStorageForCreateEvent.name);
                distributedTx.startLocalTx(ClearCartEvent.name);
                distributedTx.updateParams(ORDER_ID, event.getCommand().getOrderId());
                distributedTx.updateParams(CHANGE_ID, event.getCommand().getTxId());
                distributedTx.updateParams(COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                DomainEventPublisher.instance().publish(new GeneratePaymentQRLinkEvent(event.getCommand().getOrderId(), event.getCommand().getTxId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new DecreaseOrderStorageForCreateEvent(event.getCommand().getOrderId(), event.getCommand().getTxId(), distributedTx.getId(), command));
                DomainEventPublisher.instance().publish(new ClearCartEvent(command, event.getCommand().getTxId(), distributedTx.getId()));
                DomainRegistry.getDistributedTxRepository().store(distributedTx);
            }, command.getOrderId());
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(ClearCartReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handle(ClearCartEvent.name, command);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(DecreaseOrderStorageForCreateReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handle(DecreaseOrderStorageForCreateEvent.name, command);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(GeneratePaymentQRLinkReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handle(GeneratePaymentQRLinkEvent.name, command);
                SaveNewOrderEvent event = new SaveNewOrderEvent(command, CommonDomainRegistry.getCustomObjectSerializer().deserialize(e.getParameters().get(COMMAND), CommonOrderCommand.class), e.getId(), e.getParameters().get(CHANGE_ID));
                e.startLocalTx(SaveNewOrderEvent.name);
                DomainEventPublisher.instance().publish(event);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(SaveNewOrderReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handle(SaveNewOrderEvent.name, command);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CREATE_ORDER_DTX);
    }


    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(dtxId);
            byId.ifPresent(e -> e.cancel(AppConstant.CREATE_ORDER_DTX_FAILED_EVENT));
            return null;
        }, "CancelDtx");
    }

    public SumPagedRep<DistributedTx> query(String queryParam, String pageParam, String skipCount) {
        DistributedTxQuery var0 = new DistributedTxQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getDistributedTxRepository().query(var0);
    }
    public Optional<DistributedTx> query(long id) {
        return DomainRegistry.getDistributedTxRepository().getById(id);
    }

    /**
     * retry start ltx
     */
    @Transactional
    @SubscribeForEvent
    public void handle(DTXSuccessEvent deserialize) {
        DistributedTx.handle(deserialize);
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
