package com.mt.payment.application;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.distributed_lock.SagaDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.payment.application.command.CancelRemovePaymentLinkEvent;
import com.mt.payment.application.command.RemovePaymentLinkEvent;
import com.mt.payment.domain.DomainServiceRegistry;
import com.mt.payment.domain.model.Payment;
import com.mt.payment.domain.model.PaymentStatus;
import com.mt.payment.application.command.CancelPaymentQRLinkEvent;
import com.mt.payment.domain.model.event.CancelPaymentQRLinkReplyEvent;
import com.mt.payment.application.command.GeneratePaymentLinkEvent;
import com.mt.payment.domain.model.event.CancelRemovePaymentLinkReplyEvent;
import com.mt.payment.domain.model.event.GeneratePaymentLinkReplyEvent;
import com.mt.payment.domain.model.event.RemovePaymentLinkReplyEvent;
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
    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void generatePaymentLink(GeneratePaymentLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(event.getChangeId(), (changeRecordCommand -> {
            String randomStr = UUID.randomUUID().toString().replace("-", "");
            String paymentLink = "weixin：//wxpay/bizpayurl?appid=wx2421b1c4370ec43b&mch_id=10000100&nonce_str="
                    + randomStr + "&product_id=" + event.getOrderId() + "&time_stamp=1415949957&sign=512F68131DD251DA4A45DA79CC7EFE9D";
            Payment payment = new Payment("dummyOpenId", event.getOrderId());
            DomainServiceRegistry.getPaymentRepository().createOrUpdate(payment);
            changeRecordCommand.setReturnValue(paymentLink);
            return null;
        }), (ignore)->{
            DomainEventPublisher.instance().publish(new GeneratePaymentLinkReplyEvent(ignore.getReturnValue(), event.getTaskId(), ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);

    }

    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void cancelPaymentLink(CancelPaymentQRLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(event.getChangeId(), (command -> {
            Optional<Payment> payment = DomainServiceRegistry.getPaymentRepository().searchPaymentByOrderId(event.getOrderId());
            if(payment.isEmpty()){
                notifyAdmin("cancel failed bcz payment is empty",event.getChangeId(),event.getOrderId());
            }
            Payment payment1 = payment.get();
            payment1.cancel();
            DomainServiceRegistry.getPaymentRepository().createOrUpdate(payment1);
            return null;
        }), (ignore)->{
            DomainEventPublisher.instance().publish(new CancelPaymentQRLinkReplyEvent(ignore.isEmptyOpt(), event.getTaskId()));
            return null;
        }, AGGREGATE_NAME);
    }

    @Transactional
    @SubscribeForEvent
    public void handleQRCodeScan(String userId, String orderId, String changeId) {
        ApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (changeRecordCommand -> {
            Payment payment = new Payment(userId, orderId);
            DomainServiceRegistry.getPaymentRepository().createOrUpdate(payment);
            return null;
        }), AGGREGATE_NAME);
    }

    public Boolean checkPaymentStatus(String orderId) {
        Optional<Payment> paymentByOrderId = DomainServiceRegistry.getPaymentRepository().searchPaymentByOrderId(orderId);
        if (paymentByOrderId.isEmpty())
            return null;
        if (paymentByOrderId.get().getStatus().equals(PaymentStatus.PAID)) {
            return true;
        } else {
            Boolean aBoolean = DomainServiceRegistry.getWeChatPayService().checkPaymentStatus(paymentByOrderId.get().getPrepayId());
            paymentByOrderId.get().setStatus(aBoolean ? PaymentStatus.PAID : PaymentStatus.UNPAID);
            return aBoolean;
        }
    }
    private void notifyAdmin(String msg, String changeId,String orderId) {
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
    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(RemovePaymentLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(event.getChangeId(), (command -> {
            Optional<Payment> payment = DomainServiceRegistry.getPaymentRepository().searchPaymentByOrderId(event.getOrderId());
            if(payment.isEmpty()){
                notifyAdmin("remove failed bcz payment is empty",event.getChangeId(),event.getOrderId());
            }
            Payment payment1 = payment.get();
            payment1.refund();
            DomainServiceRegistry.getPaymentRepository().createOrUpdate(payment1);
            return null;
        }), (ignore)->{
            DomainEventPublisher.instance().publish(new RemovePaymentLinkReplyEvent(event.getTaskId(),ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }
    @SubscribeForEvent
    @Transactional
    @SagaDistLock(keyExpression = "#p0.changeId",aggregateName = AGGREGATE_NAME)
    public void handle(CancelRemovePaymentLinkEvent event) {
        ApplicationServiceRegistry.getIdempotentService().idempotentMsg(event.getChangeId(), (command -> {
            Optional<Payment> payment = DomainServiceRegistry.getPaymentRepository().searchPaymentByOrderId(event.getOrderId());
            if(payment.isEmpty()){
                notifyAdmin("cancel remove failed bcz payment is empty",event.getChangeId(),event.getOrderId());
            }
            Payment payment1 = payment.get();
            payment1.charge();
            DomainServiceRegistry.getPaymentRepository().createOrUpdate(payment1);
            return null;
        }), (ignore)->{
            DomainEventPublisher.instance().publish(new CancelRemovePaymentLinkReplyEvent(event.getTaskId(),ignore.isEmptyOpt()));
            return null;
        }, AGGREGATE_NAME);
    }
}
