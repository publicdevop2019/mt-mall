package com.mt.saga.appliction.cancel_reserve_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_reserve_order_dtx.command.CancelDecreaseOrderStorageForReserveReplyEvent;
import com.mt.saga.appliction.cancel_reserve_order_dtx.command.CancelUpdateOrderForReserveReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTXQuery;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTXQuery;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelDecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelUpdateOrderForReserveEvent;
import com.mt.saga.domain.model.invalid_order.event.InvalidOrderDTXFailedEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.ReserveOrderDTXFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class CancelReserveOrderDTXApplicationService {

    private static final String CANCEL_RESERVE_ORDER_DTX = "CancelReserveOrderDTX";
    private static final String CANCEL_RESERVE_ORDER_DTX_RESOLVED = "CancelReserveOrderDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(ReserveOrderDTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
            CancelReserveOrderDTX dtx = new CancelReserveOrderDTX(event);
            DomainEventPublisher.instance().publish(new CancelDecreaseOrderStorageForReserveEvent(dtx));
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForReserveEvent(dtx));
            dtx.markAsStarted();
            DomainRegistry.getCancelReserveOrderDTXRepository().createOrUpdate(dtx);

            return null;
        }, CANCEL_RESERVE_ORDER_DTX);

    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelDecreaseOrderStorageForReserveReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
        long taskId = event.getTaskId();
        Optional<CancelReserveOrderDTX> byId = DomainRegistry.getCancelReserveOrderDTXRepository().getById(taskId);
        byId.ifPresent(e -> {
            e.handle(event);
        });

            return null;
        },CANCEL_RESERVE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderForReserveReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
        long taskId = event.getTaskId();
        Optional<CancelReserveOrderDTX> byId = DomainRegistry.getCancelReserveOrderDTXRepository().getById(taskId);
        byId.ifPresent(e -> {
            e.handle(event);
        });

            return null;
        },CANCEL_RESERVE_ORDER_DTX);
    }

    public SumPagedRep<CancelReserveOrderDTX> query(String queryParam, String pageParam, String skipCount) {
        CancelReserveOrderDTXQuery var0 = new CancelReserveOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCancelReserveOrderDTXRepository().query(var0);
    }

    public Optional<CancelReserveOrderDTX> query(long id) {
        return DomainRegistry.getCancelReserveOrderDTXRepository().getById(id);
    }

    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id),(change)->{
            Optional<CancelReserveOrderDTX> byId = DomainRegistry.getCancelReserveOrderDTXRepository().getById(id);
            byId.ifPresent(e->{
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_RESERVE_ORDER_DTX_RESOLVED);
    }

}
