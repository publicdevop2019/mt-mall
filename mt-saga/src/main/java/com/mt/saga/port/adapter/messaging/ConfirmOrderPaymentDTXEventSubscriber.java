package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.conclude_order_dtx.command.OrderUpdateForConcludeFailedCommand;
import com.mt.saga.appliction.confirm_order_payment_dtx.command.OrderUpdateForPaymentSuccessFailedCommand;
import com.mt.saga.appliction.confirm_order_payment_dtx.command.UpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateConfirmOrderPaymentDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConfirmOrderPaymentDTXEventSubscriber {
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT, (event) -> {
            CreateConfirmOrderPaymentDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateConfirmOrderPaymentDTXEvent.class);
            ApplicationServiceRegistry.getConfirmOrderPaymentDTXApplicationService().create(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_PAYMENT_SUCCESS_EVENT, (event) -> {
            UpdateOrderPaymentSuccessReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderPaymentSuccessReplyEvent.class);
            ApplicationServiceRegistry.getConfirmOrderPaymentDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CANCEL_CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT, (event) -> {
            CancelConfirmOrderPaymentDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelConfirmOrderPaymentDTXSuccessEvent.class);
            ApplicationServiceRegistry.getConfirmOrderPaymentDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false,AppConstant.ORDER_UPDATE_FOR_PAYMENT_SUCCESS_FAILED, (event) -> {
            OrderUpdateForPaymentSuccessFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), OrderUpdateForPaymentSuccessFailedCommand.class);
            ApplicationServiceRegistry.getConfirmOrderPaymentDTXApplicationService().handle(deserialize);
        });
    }
}
