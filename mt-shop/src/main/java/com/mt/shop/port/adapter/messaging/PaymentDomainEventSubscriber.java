package com.mt.shop.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.payment.command.CancelPaymentQRLinkEvent;
import com.mt.shop.application.payment.command.CancelRemovePaymentLinkEvent;
import com.mt.shop.application.payment.command.GeneratePaymentLinkEvent;
import com.mt.shop.application.payment.command.RemovePaymentLinkEvent;
import com.mt.shop.infrastructure.AppConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class PaymentDomainEventSubscriber {
    @Value("${mt.app.name.mt15}")
    private String sagaName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(sagaName, false,
                AppConstant.GENERATE_PAYMENT_QR_LINK_EVENT,
                (event) -> {
                    String eventBody = event.getEventBody();
                    GeneratePaymentLinkEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(eventBody, GeneratePaymentLinkEvent.class);
                    ApplicationServiceRegistry.getPaymentApplicationService().generatePaymentLink(deserialize);
                });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaName, false,
                AppConstant.GENERATE_PAYMENT_QR_LINK_EVENT,
                (event) -> {
                    CancelPaymentQRLinkEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelPaymentQRLinkEvent.class);
                    ApplicationServiceRegistry.getPaymentApplicationService().cancelPaymentLink(deserialize);
                });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(sagaName, false,
                AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT,
                (event) -> {
                    String eventBody = event.getEventBody();
                    RemovePaymentLinkEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(eventBody, RemovePaymentLinkEvent.class);
                    ApplicationServiceRegistry.getPaymentApplicationService().handle(deserialize);
                });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaName, false,
                AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT,
                (event) -> {
                    CancelRemovePaymentLinkEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelRemovePaymentLinkEvent.class);
                    ApplicationServiceRegistry.getPaymentApplicationService().handle(deserialize);
                });
    }

}
