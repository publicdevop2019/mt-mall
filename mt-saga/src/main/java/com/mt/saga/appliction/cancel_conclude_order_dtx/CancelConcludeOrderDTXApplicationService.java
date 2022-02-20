package com.mt.saga.appliction.cancel_conclude_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_conclude_order_dtx.command.CancelDecreaseActualStorageForConcludeReplyEvent;
import com.mt.saga.appliction.cancel_conclude_order_dtx.command.CancelUpdateOrderForConcludeReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelDecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelUpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class CancelConcludeOrderDTXApplicationService {

    private static final String CANCEL_CONCLUDE_ORDER_DTX = "CancelConcludeOrderDTX";
    private static final String CANCEL_CONCLUDE_ORDER_DTX_RESOLVED = "CancelConcludeOrderDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(ConcludeOrderDTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
        CancelConcludeOrderDTX dtx = new CancelConcludeOrderDTX(event);
        DomainRegistry.getCancelConcludeOrderDTXRepository().createOrUpdate(dtx);
        DomainEventPublisher.instance().publish(new CancelUpdateOrderForConcludeEvent(dtx));
        DomainEventPublisher.instance().publish(new CancelDecreaseActualStorageForConcludeEvent(dtx));
        dtx.markAsStarted();
            return null;
        },CANCEL_CONCLUDE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelDecreaseActualStorageForConcludeReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
        Optional<CancelConcludeOrderDTX> byId = DomainRegistry.getCancelConcludeOrderDTXRepository().getById(deserialize.getTaskId());
        byId.ifPresent(e -> {
            e.handle(deserialize);
        });
            return null;
        },CANCEL_CONCLUDE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderForConcludeReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
        Optional<CancelConcludeOrderDTX> byId = DomainRegistry.getCancelConcludeOrderDTXRepository().getById(deserialize.getTaskId());
        byId.ifPresent(e -> {
            e.handle(deserialize);
        });
            return null;
        }, CANCEL_CONCLUDE_ORDER_DTX);

    }

    public SumPagedRep<CancelConcludeOrderDTX> query(String queryParam, String pageParam, String skipCount) {
        CancelConcludeOrderDTXQuery var0 = new CancelConcludeOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCancelConcludeOrderDTXRepository().query(var0);
    }

    public Optional<CancelConcludeOrderDTX> query(long id) {
        return DomainRegistry.getCancelConcludeOrderDTXRepository().getById(id);
    }

    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id),(change)->{
            Optional<CancelConcludeOrderDTX> byId = DomainRegistry.getCancelConcludeOrderDTXRepository().getById(id);
            byId.ifPresent(e->{
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_CONCLUDE_ORDER_DTX_RESOLVED);
    }
}
