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
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelClearCartEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelDecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelGeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelSaveNewOrderEvent;
import com.mt.saga.domain.model.distributed_tx.*;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CancelCreateOrderDTXApplicationService {

    public static final String APP_CANCEL_GENERATE_PAYMENT_QR_LINK = "CANCEL_GENERATE_PAYMENT_QR_LINK";
    public static final String APP_CANCEL_DECREASE_ORDER_STORAGE = "CANCEL_DECREASE_ORDER_STORAGE";
    public static final String APP_CANCEL_CLEAR_CART = "CANCEL_CLEAR_CART";
    public static final String APP_CANCEL_SAVE_NEW_ORDER = "CANCEL_SAVE_NEW_ORDER";

    private static final String CANCEL_CREATE_ORDER_DTX = "CancelCreateOrderDTX";
    private static final String CANCEL_CREATE_ORDER_DTX_RESOLVED = "CancelCreateOrderDTXResolved";

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelGeneratePaymentQRLinkReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(command);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_CANCEL_GENERATE_PAYMENT_QR_LINK, replyEvent);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_CREATE_ORDER_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelDecreaseOrderStorageForCreateReplyCommand deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(deserialize);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_CANCEL_DECREASE_ORDER_STORAGE, replyEvent);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_CREATE_ORDER_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelClearCartReplyCommand deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(deserialize);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_CANCEL_CLEAR_CART, replyEvent);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_CREATE_ORDER_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handleCommand(CancelSaveNewOrderReplyCommand deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            ReplyEvent replyEvent = new ReplyEvent(deserialize);
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(replyEvent.getTaskId());
            byId.ifPresent(e -> {
                e.handle(APP_CANCEL_SAVE_NEW_ORDER, replyEvent);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_CREATE_ORDER_DTX);
    }

    public SumPagedRep<DistributedTx> query(String queryParam, String pageParam, String skipCount) {
        DistributedTxQuery var0 = new DistributedTxQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getDistributedTxRepository().query(var0);
    }

    public Optional<DistributedTx> query(long id) {
        return DomainRegistry.getDistributedTxRepository().getById(id);
    }

    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(id);
            byId.ifPresent(e -> {
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_CREATE_ORDER_DTX_RESOLVED);
    }

    @Transactional
    @SubscribeForEvent
    public void handleCommand(DTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            LocalTx localTx1 = new LocalTx(APP_CANCEL_GENERATE_PAYMENT_QR_LINK, AppConstant.GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT);
            LocalTx localTx2 = new LocalTx(APP_CANCEL_DECREASE_ORDER_STORAGE, AppConstant.DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT);
            LocalTx localTx3 = new LocalTx(APP_CANCEL_CLEAR_CART, AppConstant.CLEAR_CART_FOR_CREATE_EVENT);
            LocalTx localTx4 = new LocalTx(APP_CANCEL_SAVE_NEW_ORDER, AppConstant.SAVE_NEW_ORDER_FOR_CREATE_EVENT);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            localTxes.add(localTx2);
            localTxes.add(localTx3);
            localTxes.add(localTx4);
            DistributedTx dtx = DistributedTx.cancelOf(event.getDistributedTx(), localTxes);
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get("COMMAND"), CommonOrderCommand.class);
            log.info("start of cancel task of {} with changeId {}", dtx.getId(), dtx.getChangeId());
            DomainEventPublisher.instance().publish(new CancelGeneratePaymentQRLinkEvent(event.getDistributedTx().getParameters().get("ORDER_ID"), dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelDecreaseOrderStorageForCreateEvent(command, event.getDistributedTx().getParameters().get("ORDER_ID"), dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelClearCartEvent(command, dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelSaveNewOrderEvent(command, dtx.getChangeId(), dtx.getId()));
            dtx.startAllLocalTx();
            DomainRegistry.getDistributedTxRepository().store(dtx);
            return null;
        }, CANCEL_CREATE_ORDER_DTX);
    }
}
