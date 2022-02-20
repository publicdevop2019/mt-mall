package com.mt.saga.appliction.invalid_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.invalid_order_dtx.command.*;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelInvalidOrderDTXSuccessEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTXQuery;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTXQuery;
import com.mt.saga.domain.model.order_state_machine.event.CreateInvalidOrderDTXEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class InvalidOrderDTXDTXApplicationService {
    private static final String INVALID_ORDER_DTX = "INVALID_ORDER_DTX";

    public SumPagedRep<InvalidOrderDTX> query(String queryParam, String pageParam, String skipCount) {
        InvalidOrderDTXQuery var0 = new InvalidOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getInvalidOrderDTXRepository().query(var0);
    }

    public Optional<InvalidOrderDTX> query(long id) {
        return DomainRegistry.getInvalidOrderDTXRepository().getById(id);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(CreateInvalidOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            CommonOrderCommand command = event.getCommand();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                InvalidOrderDTX dtx = new InvalidOrderDTX(event);
                DomainRegistry.getInvalidOrderDTXRepository().createOrUpdate(dtx);
            }, command.getOrderId());
            return null;
        }, INVALID_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(RemoveOrderForInvalidReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<InvalidOrderDTX> byIdLocked = DomainRegistry.getInvalidOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
                DomainRegistry.getInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, INVALID_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(RestoreCartForInvalidReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<InvalidOrderDTX> byIdLocked = DomainRegistry.getInvalidOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
                DomainRegistry.getInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, INVALID_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(IncreaseStorageForInvalidReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<InvalidOrderDTX> byIdLocked = DomainRegistry.getInvalidOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
                DomainRegistry.getInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, INVALID_ORDER_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(RemovePaymentQRLinkForInvalidReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<InvalidOrderDTX> byIdLocked = DomainRegistry.getInvalidOrderDTXRepository().getById(event.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(event);
                DomainRegistry.getInvalidOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        }, INVALID_ORDER_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<InvalidOrderDTX> byId = DomainRegistry.getInvalidOrderDTXRepository().getById(dtxId);
            byId.ifPresent(InvalidOrderDTX::cancel);
            return null;
        }, "SystemCancelInvalidOrderDtx");
    }

    public void handle(CancelInvalidOrderDTXSuccessEvent deserialize) {
        Optional<InvalidOrderDTX> byId = DomainRegistry.getInvalidOrderDTXRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(InvalidOrderDTX::retryStartedLtx);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForInvalidFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
    @Transactional
    @SubscribeForEvent
    public void handle(RestoreCartForInvalidFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
