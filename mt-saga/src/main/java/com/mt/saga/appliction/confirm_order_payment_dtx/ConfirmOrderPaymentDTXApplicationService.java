package com.mt.saga.appliction.confirm_order_payment_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.confirm_order_payment_dtx.command.OrderUpdateForPaymentSuccessFailedCommand;
import com.mt.saga.appliction.confirm_order_payment_dtx.command.UpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTXQuery;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXFailedEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.UpdateOrderPaymentSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateConfirmOrderPaymentDTXEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class ConfirmOrderPaymentDTXApplicationService {

    public static final String CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT = "CreateConfirmOrderPaymentDTXEvent";

    @SubscribeForEvent
    @Transactional
    public void create(CreateConfirmOrderPaymentDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored)->{
                ConfirmOrderPaymentDTX dtx = new ConfirmOrderPaymentDTX(event);
                DomainEventPublisher.instance().publish(new UpdateOrderPaymentSuccessEvent(dtx));
                dtx.markAsStarted();
                DomainRegistry.getConfirmOrderPaymentDTXRepository().createOrUpdate(dtx);
            },orderId);

            return null;
        }, CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    @Transactional
    public void handle(UpdateOrderPaymentSuccessReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(),(change)->{

        Optional<ConfirmOrderPaymentDTX> byId = DomainRegistry.getConfirmOrderPaymentDTXRepository().getById(event.getTaskId());
        byId.ifPresent(e -> {
            e.updateStatus(event);
        });
            return null;
        },CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<ConfirmOrderPaymentDTX> byId = DomainRegistry.getConfirmOrderPaymentDTXRepository().getById(dtxId);
            byId.ifPresent(ConfirmOrderPaymentDTX::cancel);
            return null;
        }, "SystemCancelConfirmOrderPaymentDtx");
    }

    public SumPagedRep<ConfirmOrderPaymentDTX> query(String queryParam, String pageParam, String skipCount) {
        ConfirmOrderPaymentDTXQuery var0 = new ConfirmOrderPaymentDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getConfirmOrderPaymentDTXRepository().query(var0);
    }

    public Optional<ConfirmOrderPaymentDTX> query(long id) {
        return DomainRegistry.getConfirmOrderPaymentDTXRepository().getById(id);
    }

    public void handle(CancelConfirmOrderPaymentDTXSuccessEvent deserialize) {
        Optional<ConfirmOrderPaymentDTX> byId = DomainRegistry.getConfirmOrderPaymentDTXRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(ConfirmOrderPaymentDTX::retryStartedLtx);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForPaymentSuccessFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
