package com.mt.saga.appliction.conclude_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.conclude_order_dtx.command.DecreaseActualStorageForConcludeReplyEvent;
import com.mt.saga.appliction.conclude_order_dtx.command.OrderUpdateForConcludeFailedCommand;
import com.mt.saga.appliction.conclude_order_dtx.command.UpdateOrderForConcludeReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTXQuery;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXFailedEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.DecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.UpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateConcludeOrderDTXEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class ConcludeOrderDTXApplicationService {

    @Transactional
    @SubscribeForEvent
    public void handle(CreateConcludeOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                ConcludeOrderDTX concludeOrderDTX = new ConcludeOrderDTX(event);
                DomainEventPublisher.instance().publish(new UpdateOrderForConcludeEvent(concludeOrderDTX));
                DomainEventPublisher.instance().publish(new DecreaseActualStorageForConcludeEvent(concludeOrderDTX));
                concludeOrderDTX.markAsStarted();
                DomainRegistry.getConcludeOrderDTXRepository().createOrUpdate(concludeOrderDTX);

            }, orderId);
            return null;
        }, "ConcludeOrderDTX");
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(UpdateOrderForConcludeReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<ConcludeOrderDTX> byIdLocked = DomainRegistry.getConcludeOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
            });

            return null;
        }, "ConcludeOrderDTX");
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(DecreaseActualStorageForConcludeReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {

            Optional<ConcludeOrderDTX> byIdLocked = DomainRegistry.getConcludeOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
            });
            return null;
        }, "ConcludeOrderDTX");
    }

    public SumPagedRep<ConcludeOrderDTX> query(String queryParam, String pageParam, String skipCount) {

        ConcludeOrderDTXQuery var0 = new ConcludeOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getConcludeOrderDTXRepository().query(var0);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<ConcludeOrderDTX> byId = DomainRegistry.getConcludeOrderDTXRepository().getById(dtxId);
            byId.ifPresent(ConcludeOrderDTX::cancel);
            return null;
        }, "SystemCancelConcludeOrderDtx");
    }

    public Optional<ConcludeOrderDTX> query(long id) {
        return DomainRegistry.getConcludeOrderDTXRepository().getById(id);
    }

    public void handle(CancelConcludeOrderDTXSuccessEvent deserialize) {
        Optional<ConcludeOrderDTX> byId = DomainRegistry.getConcludeOrderDTXRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(ConcludeOrderDTX::retryStartedLtx);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForConcludeFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
