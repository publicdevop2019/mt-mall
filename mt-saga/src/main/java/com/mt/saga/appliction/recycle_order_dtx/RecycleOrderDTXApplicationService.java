package com.mt.saga.appliction.recycle_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.recycle_order_dtx.command.IncreaseOrderStorageForRecycleReplyEvent;
import com.mt.saga.appliction.recycle_order_dtx.command.OrderUpdateForRecycleFailedCommand;
import com.mt.saga.appliction.recycle_order_dtx.command.UpdateOrderForRecycleReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelRecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateRecycleOrderDTXEvent;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTXQuery;
import com.mt.saga.domain.model.recycle_order_dtx.event.IncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXFailedEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.UpdateOrderForRecycleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class RecycleOrderDTXApplicationService {

    @Transactional
    @SubscribeForEvent
    public void handle(CreateRecycleOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                RecycleOrderDTX recycleOrderDTX = new RecycleOrderDTX(event);
                DomainRegistry.getRecycleOrderDTXRepository().createOrUpdate(recycleOrderDTX);
                DomainEventPublisher.instance().publish(new IncreaseOrderStorageForRecycleEvent(recycleOrderDTX));
                DomainEventPublisher.instance().publish(new UpdateOrderForRecycleEvent(recycleOrderDTX));
                recycleOrderDTX.markAsStarted();
            }, orderId);
            return null;
        }, "RecycleOrderDTX");
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(UpdateOrderForRecycleReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<RecycleOrderDTX> byIdLocked = DomainRegistry.getRecycleOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
            });
            return null;
        }, "RecycleOrderDTX");
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(IncreaseOrderStorageForRecycleReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<RecycleOrderDTX> byIdLocked = DomainRegistry.getRecycleOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
            });
            return null;
        }, "RecycleOrderDTX");
    }

    public SumPagedRep<RecycleOrderDTX> query(String queryParam, String pageParam, String skipCount) {

        RecycleOrderDTXQuery var0 = new RecycleOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getRecycleOrderDTXRepository().query(var0);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        log.debug("start of cancel dtx {}", dtxId);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<RecycleOrderDTX> byId = DomainRegistry.getRecycleOrderDTXRepository().getById(dtxId);
            byId.ifPresent(RecycleOrderDTX::cancel);
            return null;
        }, "SystemCancelRecycleOrderDtx");
    }

    public Optional<RecycleOrderDTX> query(long id) {
        return DomainRegistry.getRecycleOrderDTXRepository().getById(id);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(CancelRecycleOrderDTXSuccessEvent deserialize) {
        Optional<RecycleOrderDTX> byId = DomainRegistry.getRecycleOrderDTXRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(RecycleOrderDTX::retryStartedLtx);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForRecycleFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
