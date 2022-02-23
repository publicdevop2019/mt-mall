package com.mt.saga.appliction.reserve_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.reserve_order_dtx.command.DecreaseOrderStorageForReserveReplyEvent;
import com.mt.saga.appliction.reserve_order_dtx.command.OrderUpdateForReserveFailedCommand;
import com.mt.saga.appliction.reserve_order_dtx.command.UpdateOrderForReserveReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateReserveOrderDTXEvent;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTXQuery;
import com.mt.saga.domain.model.reserve_order_dtx.event.DecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.UpdateOrderForReserveEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class ReserveOrderDTXApplicationService {

    @Transactional
    @SubscribeForEvent
    public void handle(CreateReserveOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored)->{
                log.debug("start of creating {}", CreateReserveOrderDTXEvent.class.getName());
                ReserveOrderDTX dtx = new ReserveOrderDTX(event);
                DomainEventPublisher.instance().publish(new DecreaseOrderStorageForReserveEvent(dtx));
                DomainEventPublisher.instance().publish(new UpdateOrderForReserveEvent(dtx));
                dtx.markAsStarted();
                DomainRegistry.getReserveOrderDTXRepository().createOrUpdate(dtx);
            },orderId);
            return null;
        }, "ReserveOrderDTX");
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(UpdateOrderForReserveReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            long taskId = event.getTaskId();
            Optional<ReserveOrderDTX> byIdLocked = DomainRegistry.getReserveOrderDTXRepository().getById(taskId);
            byIdLocked.ifPresent(e -> {
                e.updateOrderLTXStatus(event);
            });
            return null;
        }, "ReserveOrderDTX");
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(DecreaseOrderStorageForReserveReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            long taskId = event.getTaskId();
            Optional<ReserveOrderDTX> byIdLocked = DomainRegistry.getReserveOrderDTXRepository().getById(taskId);
            byIdLocked.ifPresent(e -> {
                e.updateSkuLTXStatus(event);
            });
            return null;
        }, "ReserveOrderDTX");
    }

    public SumPagedRep<ReserveOrderDTX> query(String queryParam, String pageParam, String skipCount) {

        ReserveOrderDTXQuery var0 = new ReserveOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getReserveOrderDTXRepository().query(var0);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<ReserveOrderDTX> byId = DomainRegistry.getReserveOrderDTXRepository().getById(dtxId);
            byId.ifPresent(ReserveOrderDTX::cancel);
            return null;
        }, "SystemCancelReserveOrderDtx");
    }

    public Optional<ReserveOrderDTX> query(long id) {
        return DomainRegistry.getReserveOrderDTXRepository().getById(id);
    }

    public void handle(CancelReserveOrderDTXSuccessEvent deserialize) {
        Optional<ReserveOrderDTX> byId = DomainRegistry.getReserveOrderDTXRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(ReserveOrderDTX::retryStartedLtx);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForReserveFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
