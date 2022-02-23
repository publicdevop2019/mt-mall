package com.mt.saga.appliction.cancel_recycle_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelIncreaseOrderStorageForRecycleReplyEvent;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelUpdateOrderForRecycleReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTXQuery;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelIncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelUpdateOrderForRecycleEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class CancelRecycleOrderDTXApplicationService {

    private static final String CANCEL_RECYCLE_ORDER_DTX = "CancelRecycleOrderDTX";
    private static final String CANCEL_RECYCLE_ORDER_DTX_RESOLVED = "CancelRecycleOrderDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(RecycleOrderDTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{

        CancelRecycleOrderDTX cancelRecycleOrderDTX = new CancelRecycleOrderDTX(event);
        DomainEventPublisher.instance().publish(new CancelIncreaseOrderStorageForRecycleEvent(cancelRecycleOrderDTX));
        DomainEventPublisher.instance().publish(new CancelUpdateOrderForRecycleEvent(cancelRecycleOrderDTX));
        cancelRecycleOrderDTX.markAsStarted();
        DomainRegistry.getCancelRecycleOrderDTXRepository().createOrUpdate(cancelRecycleOrderDTX);
            return null;
        }, CANCEL_RECYCLE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderForRecycleReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{

        long taskId = event.getTaskId();
        Optional<CancelRecycleOrderDTX> byId = DomainRegistry.getCancelRecycleOrderDTXRepository().getById(taskId);
        byId.ifPresent(e -> {
            e.handle(event);
        });
            return null;
        },CANCEL_RECYCLE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelIncreaseOrderStorageForRecycleReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
        long taskId = event.getTaskId();
        Optional<CancelRecycleOrderDTX> byId = DomainRegistry.getCancelRecycleOrderDTXRepository().getById(taskId);
        byId.ifPresent(e -> {
            e.handle(event);
        });
            return null;
        },CANCEL_RECYCLE_ORDER_DTX);

    }

    public SumPagedRep<CancelRecycleOrderDTX> query(String queryParam, String pageParam, String skipCount) {
        CancelRecycleOrderDTXQuery var0 = new CancelRecycleOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCancelRecycleOrderDTXRepository().query(var0);
    }

    public Optional<CancelRecycleOrderDTX> query(long id) {
        return DomainRegistry.getCancelRecycleOrderDTXRepository().getById(id);
    }

    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id),(change)->{
            Optional<CancelRecycleOrderDTX> byId = DomainRegistry.getCancelRecycleOrderDTXRepository().getById(id);
            byId.ifPresent(e->{
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_RECYCLE_ORDER_DTX_RESOLVED);
    }
}
