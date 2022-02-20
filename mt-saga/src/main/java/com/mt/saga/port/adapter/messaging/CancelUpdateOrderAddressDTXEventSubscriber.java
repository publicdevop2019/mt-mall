package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command.CancelUpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.appliction.cancel_update_order_address_dtx.command.CancelUpdateOrderAddressReplyEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXFailedEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderAddressDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelUpdateOrderAddressDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profile;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.UPDATE_ORDER_ADDRESS_DTX_FAILED_EVENT, (event) -> {
            UpdateOrderAddressDTXFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderAddressDTXFailedEvent.class);
            ApplicationServiceRegistry.getCancelUpdateOrderAddressDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profile, false, AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT , (event) -> {
            CancelUpdateOrderAddressReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderAddressReplyEvent.class);
            ApplicationServiceRegistry.getCancelUpdateOrderAddressDTXApplicationService().handle(deserialize);
        });
    }
}
