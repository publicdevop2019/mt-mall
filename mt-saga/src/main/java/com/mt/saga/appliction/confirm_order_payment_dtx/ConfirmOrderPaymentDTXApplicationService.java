package com.mt.saga.appliction.confirm_order_payment_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.confirm_order_payment_dtx.command.OrderUpdateForPaymentSuccessFailedCommand;
import com.mt.saga.appliction.confirm_order_payment_dtx.command.UpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.conclude_order_dtx.event.UpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.UpdateOrderPaymentSuccessEvent;
import com.mt.saga.domain.model.distributed_tx.DTXSuccessEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.order_state_machine.event.CreateConfirmOrderPaymentDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mt.saga.appliction.create_order_dtx.CreateOrderDTXApplicationService.DTX_COMMAND;

@Service
@Slf4j
public class ConfirmOrderPaymentDTXApplicationService {

    public static final String CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT = "CreateConfirmOrderPaymentDTXEvent";

    @SubscribeForEvent
    @Transactional
    public void create(CreateConfirmOrderPaymentDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {

                LocalTx localTx1 = new LocalTx(UpdateOrderPaymentSuccessEvent.name, UpdateOrderPaymentSuccessEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                DistributedTx distributedTx = new DistributedTx(localTxes, "ConfirmOrderPaymentOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId());
                distributedTx.startLocalTx(UpdateOrderPaymentSuccessEvent.name);
                distributedTx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();

                DomainEventPublisher.instance().publish(new UpdateOrderPaymentSuccessEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainRegistry.getDistributedTxRepository().store(distributedTx);
            }, orderId);

            return null;
        }, CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    @Transactional
    public void handle(UpdateOrderPaymentSuccessReplyEvent command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {

            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handle(UpdateOrderForConcludeEvent.name, command);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(dtxId);
            byId.ifPresent(e -> e.cancel(AppConstant.CONFIRM_ORDER_PAYMENT_FAILED_EVENT));
            return null;
        }, "SystemCancelConfirmOrderPaymentDtx");
    }


    public SumPagedRep<DistributedTx> query(String queryParam, String pageParam, String skipCount) {
        DistributedTxQuery var0 = new DistributedTxQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getDistributedTxRepository().query(var0);
    }

    public Optional<DistributedTx> query(long id) {
        return DomainRegistry.getDistributedTxRepository().getById(id);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(DTXSuccessEvent deserialize) {
        DistributedTx.handle(deserialize);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForPaymentSuccessFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
