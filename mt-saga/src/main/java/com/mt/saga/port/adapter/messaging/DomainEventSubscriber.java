package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.distributed_tx.DTXSuccessEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_OPERATION_EVENT, (event) -> {
            CommonOrderCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CommonOrderCommand.class);
            log.debug("event detail {}", deserialize);
            ApplicationServiceRegistry.getStateMachineApplicationService().start(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT, (event) -> {
            ConfirmOrderPaymentDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ConfirmOrderPaymentDTXSuccessEvent.class);
            ApplicationServiceRegistry.getStateMachineApplicationService().start(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener9() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.DTX_SUCCESS_EVENT), (event) -> {
            DTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CREATE_ORDER_DTX_SUCCESS_EVENT);
    }


    private String getValidatorQueueName(String eventName) {
        return "post_" + eventName + "_validation_handler";
    }


}
