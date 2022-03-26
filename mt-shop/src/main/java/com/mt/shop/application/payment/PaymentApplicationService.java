package com.mt.shop.application.payment;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.distributed_lock.SagaDistLock;

import com.mt.common.domain.model.domain_event.StoredEvent;

import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.payment.command.CancelPaymentQRLinkEvent;
import com.mt.shop.application.payment.command.CancelRemovePaymentLinkEvent;
import com.mt.shop.application.payment.command.GeneratePaymentLinkEvent;
import com.mt.shop.application.payment.command.RemovePaymentLinkEvent;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.payment.Payment;
import com.mt.shop.domain.model.payment.PaymentStatus;
import com.mt.shop.domain.model.payment.event.CancelPaymentQRLinkReplyEvent;
import com.mt.shop.domain.model.payment.event.CancelRemovePaymentLinkReplyEvent;
import com.mt.shop.domain.model.payment.event.GeneratePaymentLinkReplyEvent;
import com.mt.shop.domain.model.payment.event.RemovePaymentLinkReplyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.mt.common.domain.model.constant.AppInfo.EventName.MT6_PAYMENT_UPDATE_FAILED;

@Slf4j
@Service
public class PaymentApplicationService {

    public static final String AGGREGATE_NAME = "payment";
    @Value("${spring.application.name}")
    private String appName;

    
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void generatePaymentLink(GeneratePaymentLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(event.getChangeId(), (changeRecordCommand -> {
            String randomStr = UUID.randomUUID().toString().replace("-", "");
            String paymentLink = "weixin：//wxpay/bizpayurl?appid=wx2421b1c4370ec43b&mch_id=10000100&nonce_str="
                    + randomStr + "&product_id=" + event.getOrderId() + "&time_stamp=1415949957&sign=512F68131DD251DA4A45DA79CC7EFE9D";
            Payment payment = new Payment("dummyOpenId", event.getOrderId());
            DomainRegistry.getPaymentRepository().createOrUpdate(payment);
            changeRecordCommand.setReturnValue(paymentLink);
            return null;
        }), (ignore) -> {
            CommonDomainRegistry.getDomainEventRepository().append(new GeneratePaymentLinkReplyEvent(ignore.getReturnValue(), event.getTaskId(), ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);

    }

    
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void cancelPaymentLink(CancelPaymentQRLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(event.getChangeId(), (command -> {
            Optional<Payment> payment = DomainRegistry.getPaymentRepository().searchPaymentByOrderId(event.getOrderId());
            if (payment.isEmpty()) {
                notifyAdmin("cancel failed bcz payment is empty", event.getChangeId(), event.getOrderId());
            }
            Payment payment1 = payment.get();
            payment1.cancel();
            DomainRegistry.getPaymentRepository().createOrUpdate(payment1);
            return null;
        }), (ignore) -> {
            CommonDomainRegistry.getDomainEventRepository().append(new CancelPaymentQRLinkReplyEvent(ignore.isEmptyOpt(), event.getTaskId()));
            return null;
        }, AGGREGATE_NAME);
    }

    @Transactional
    
    public void handleQRCodeScan(String userId, String orderId, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (changeRecordCommand -> {
            Payment payment = new Payment(userId, orderId);
            DomainRegistry.getPaymentRepository().createOrUpdate(payment);
            return null;
        }), AGGREGATE_NAME);
    }

    public Boolean checkPaymentStatus(String orderId) {
        Optional<Payment> paymentByOrderId = DomainRegistry.getPaymentRepository().searchPaymentByOrderId(orderId);
        if (paymentByOrderId.isEmpty())
            return null;
        if (paymentByOrderId.get().getStatus().equals(PaymentStatus.PAID)) {
            return true;
        } else {
            Boolean aBoolean = DomainRegistry.getWeChatPayService().checkPaymentStatus(paymentByOrderId.get().getPrepayId());
            paymentByOrderId.get().setStatus(aBoolean ? PaymentStatus.PAID : PaymentStatus.UNPAID);
            return aBoolean;
        }
    }

    private void notifyAdmin(String msg, String changeId, String orderId) {
        //directly publish msg to stream
        MallNotificationEvent event = MallNotificationEvent.create(MT6_PAYMENT_UPDATE_FAILED);
        event.setChangeId(changeId);
        event.setOrderId(orderId);
        event.addDetail(AppInfo.MISC.MESSAGE,
                msg);
        StoredEvent storedEvent = new StoredEvent(event);
        storedEvent.setIdExplicitly(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        CommonDomainRegistry.getEventStreamService().next(appName, event.isInternal(), event.getTopic(), storedEvent);
    }

    
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void handle(RemovePaymentLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(event.getChangeId(), (command -> {
            Optional<Payment> payment = DomainRegistry.getPaymentRepository().searchPaymentByOrderId(event.getOrderId());
            if (payment.isEmpty()) {
                notifyAdmin("remove failed bcz payment is empty", event.getChangeId(), event.getOrderId());
            }
            Payment payment1 = payment.get();
            payment1.refund();
            DomainRegistry.getPaymentRepository().createOrUpdate(payment1);
            return null;
        }), (ignore) -> {
            CommonDomainRegistry.getDomainEventRepository().append(new RemovePaymentLinkReplyEvent(event.getTaskId(), ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }

    
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = AGGREGATE_NAME)
    public void handle(CancelRemovePaymentLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotentMsg(event.getChangeId(), (command -> {
            Optional<Payment> payment = DomainRegistry.getPaymentRepository().searchPaymentByOrderId(event.getOrderId());
            if (payment.isEmpty()) {
                notifyAdmin("cancel remove failed bcz payment is empty", event.getChangeId(), event.getOrderId());
            }
            Payment payment1 = payment.get();
            payment1.charge();
            DomainRegistry.getPaymentRepository().createOrUpdate(payment1);
            return null;
        }), (ignore) -> {
            CommonDomainRegistry.getDomainEventRepository().append(new CancelRemovePaymentLinkReplyEvent(event.getTaskId(), ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }
}
