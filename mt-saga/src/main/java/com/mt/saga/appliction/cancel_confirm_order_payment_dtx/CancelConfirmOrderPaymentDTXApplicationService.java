package com.mt.saga.appliction.cancel_confirm_order_payment_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command.CancelUpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTXQuery;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelUpdateOrderPaymentEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j

public class CancelConfirmOrderPaymentDTXApplicationService {

    private static final String CANCEL_CONFIRM_ORDER_PAYMENT_DTX = "CancelConfirmOrderPaymentDTX";
    private static final String CANCEL_CONFIRM_ORDER_PAYMENT_DTX_RESOLVED = "CancelConfirmOrderPaymentDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(ConfirmOrderPaymentDTXFailedEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            CancelConfirmOrderPaymentDTX dtx = new CancelConfirmOrderPaymentDTX(deserialize);
            DomainRegistry.getCancelConfirmOrderPaymentDTXRepository().createOrUpdate(dtx);
            DomainEventPublisher.instance().publish(new CancelUpdateOrderPaymentEvent(dtx));
            dtx.markAsStarted();
            DomainRegistry.getCancelConfirmOrderPaymentDTXRepository().createOrUpdate(dtx);
            return null;
        },CANCEL_CONFIRM_ORDER_PAYMENT_DTX);
    }
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderPaymentSuccessReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            Optional<CancelConfirmOrderPaymentDTX> byId = DomainRegistry.getCancelConfirmOrderPaymentDTXRepository().getById(deserialize.getTaskId());
            byId.ifPresent(e -> {
                e.updateStatus(deserialize);
            });
            return null;
        }, CANCEL_CONFIRM_ORDER_PAYMENT_DTX);
    }

    public SumPagedRep<CancelConfirmOrderPaymentDTX> query(String queryParam, String pageParam, String skipCount) {
        CancelConfirmOrderPaymentDTXQuery var0 = new CancelConfirmOrderPaymentDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCancelConfirmOrderPaymentDTXRepository().query(var0);
    }

    public Optional<CancelConfirmOrderPaymentDTX> query(long id) {
        return DomainRegistry.getCancelConfirmOrderPaymentDTXRepository().getById(id);
    }
    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id),(change)->{
            Optional<CancelConfirmOrderPaymentDTX> byId = DomainRegistry.getCancelConfirmOrderPaymentDTXRepository().getById(id);
            byId.ifPresent(e->{
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_CONFIRM_ORDER_PAYMENT_DTX_RESOLVED);
    }
}
