package com.mt.saga.appliction.cancel_invalid_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_invalid_order_dtx.command.*;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTXQuery;
import com.mt.saga.domain.model.invalid_order.event.InvalidOrderDTXFailedEvent;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CancelInvalidOrderDTXDTXApplicationService {
    private static final String CANCEL_INVALID_ORDER_DTX = "CANCEL_INVALID_ORDER_DTX";
    private static final String CANCEL_INVALID_ORDER_DTX_RESOLVED = "CANCEL_INVALID_ORDER_DTX_RESOLVED";
    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id),(change)->{
            Optional<CancelInvalidOrderDTX> byId = DomainRegistry.getCancelInvalidOrderDTXRepository().getById(id);
            byId.ifPresent(e-> e.markAsResolved(resolveReason));
            return null;
        }, CANCEL_INVALID_ORDER_DTX_RESOLVED);
    }

    public SumPagedRep<CancelInvalidOrderDTX> query(String queryParam, String pageParam, String skipCount) {
        CancelInvalidOrderDTXQuery var0 = new CancelInvalidOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCancelInvalidOrderDTXRepository().query(var0);
    }

    public Optional<CancelInvalidOrderDTX> query(long id) {
        return DomainRegistry.getCancelInvalidOrderDTXRepository().getById(id);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(InvalidOrderDTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
            CancelInvalidOrderDTX dtx = new CancelInvalidOrderDTX(event);
            DomainRegistry.getCancelInvalidOrderDTXRepository().createOrUpdate(dtx);
            return null;
        },CANCEL_INVALID_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelIncreaseStorageForInvalidReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            Optional<CancelInvalidOrderDTX> byIdLocked = DomainRegistry.getCancelInvalidOrderDTXRepository().getById(deserialize.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(deserialize);
                DomainRegistry.getCancelInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CANCEL_INVALID_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelRemoveOrderForInvalidReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            Optional<CancelInvalidOrderDTX> byIdLocked = DomainRegistry.getCancelInvalidOrderDTXRepository().getById(deserialize.getTaskId());
            byIdLocked.ifPresent(e -> {

                e.handle(deserialize);
                DomainRegistry.getCancelInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CANCEL_INVALID_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelRestoreCartForInvalidReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            Optional<CancelInvalidOrderDTX> byIdLocked = DomainRegistry.getCancelInvalidOrderDTXRepository().getById(deserialize.getTaskId());
            byIdLocked.ifPresent(e -> {

                e.handle(deserialize);
                DomainRegistry.getCancelInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CANCEL_INVALID_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelRemovePaymentQRLinkForInvalidReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            Optional<CancelInvalidOrderDTX> byIdLocked = DomainRegistry.getCancelInvalidOrderDTXRepository().getById(deserialize.getTaskId());
            byIdLocked.ifPresent(e -> {

                e.handle(deserialize);
                DomainRegistry.getCancelInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, CANCEL_INVALID_ORDER_DTX);
    }
}
