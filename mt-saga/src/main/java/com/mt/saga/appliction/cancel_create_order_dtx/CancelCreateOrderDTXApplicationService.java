package com.mt.saga.appliction.cancel_create_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelClearCartReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelDecreaseOrderStorageForCreateReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelGeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelSaveNewOrderReplyCommand;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTXQuery;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelClearCartEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelDecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelGeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelSaveNewOrderEvent;
import com.mt.saga.domain.model.create_order_dtx.event.CreateOrderDTXFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class CancelCreateOrderDTXApplicationService {

    private static final String CANCEL_CREATE_ORDER_DTX = "CancelCreateOrderDTX";
    private static final String CANCEL_CREATE_ORDER_DTX_RESOLVED = "CancelCreateOrderDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handleCommand(CreateOrderDTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
            CancelCreateOrderDTX dtx = new CancelCreateOrderDTX(event);
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getOrderCommandDetail(), CommonOrderCommand.class);
            log.info("start of cancel task of {} with changeId {}", dtx.getId(), dtx.getChangeId());
            DomainEventPublisher.instance().publish(new CancelGeneratePaymentQRLinkEvent(dtx));
            DomainEventPublisher.instance().publish(new CancelDecreaseOrderStorageForCreateEvent(command,dtx));
            DomainEventPublisher.instance().publish(new CancelClearCartEvent(command,dtx));
            DomainEventPublisher.instance().publish(new CancelSaveNewOrderEvent(command,dtx));
            dtx.markAsStarted();
            DomainRegistry.getCancelCreateOrderDTXRepository().createOrUpdate(dtx);
            return null;
        },CANCEL_CREATE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelGeneratePaymentQRLinkReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(),(change)->{
            Optional<CancelCreateOrderDTX> byIdLocked = DomainRegistry.getCancelCreateOrderDTXRepository().getById(command.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(command);
                DomainRegistry.getCancelCreateOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        },CANCEL_CREATE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelDecreaseOrderStorageForCreateReplyCommand deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            Optional<CancelCreateOrderDTX> byIdLocked = DomainRegistry.getCancelCreateOrderDTXRepository().getById(deserialize.getTaskId());
            byIdLocked.ifPresent(e -> {
                e.handle(deserialize);
                DomainRegistry.getCancelCreateOrderDTXRepository().createOrUpdate(e);
            });
            return null;
        },CANCEL_CREATE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelClearCartReplyCommand deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
        Optional<CancelCreateOrderDTX> byIdLocked = DomainRegistry.getCancelCreateOrderDTXRepository().getById(deserialize.getTaskId());
        byIdLocked.ifPresent(e -> {
            e.handle(deserialize);
            DomainRegistry.getCancelCreateOrderDTXRepository().createOrUpdate(e);
        });
            return null;
        },CANCEL_CREATE_ORDER_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelSaveNewOrderReplyCommand deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
        Optional<CancelCreateOrderDTX> byIdLocked = DomainRegistry.getCancelCreateOrderDTXRepository().getById(deserialize.getTaskId());
        byIdLocked.ifPresent(e -> {

            e.handle(deserialize);
            DomainRegistry.getCancelCreateOrderDTXRepository().createOrUpdate(e);
        });
            return null;
        }, CANCEL_CREATE_ORDER_DTX);
    }

    public SumPagedRep<CancelCreateOrderDTX> query(String queryParam, String pageParam, String skipCount) {
        CancelCreateOrderDTXQuery var0 = new CancelCreateOrderDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCancelCreateOrderDTXRepository().query(var0);
    }

    public Optional<CancelCreateOrderDTX> query(long id) {
        return DomainRegistry.getCancelCreateOrderDTXRepository().getById(id);
    }

    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id),(change)->{
            Optional<CancelCreateOrderDTX> byId = DomainRegistry.getCancelCreateOrderDTXRepository().getById(id);
            byId.ifPresent(e->{
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_CREATE_ORDER_DTX_RESOLVED);
    }
}
