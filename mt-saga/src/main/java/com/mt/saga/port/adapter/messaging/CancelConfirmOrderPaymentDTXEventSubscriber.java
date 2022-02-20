package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command.CancelUpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelConfirmOrderPaymentDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profile;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CONFIRM_ORDER_PAYMENT_FAILED_EVENT, (event) -> {
            ConfirmOrderPaymentDTXFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ConfirmOrderPaymentDTXFailedEvent.class);
            ApplicationServiceRegistry.getCancelConfirmOrderPaymentDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profile, false, AppConstant.UPDATE_ORDER_FOR_PAYMENT_SUCCESS_EVENT , (event) -> {
            CancelUpdateOrderPaymentSuccessReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderPaymentSuccessReplyEvent.class);
            ApplicationServiceRegistry.getCancelConfirmOrderPaymentDTXApplicationService().handle(deserialize);
        });
    }
}
