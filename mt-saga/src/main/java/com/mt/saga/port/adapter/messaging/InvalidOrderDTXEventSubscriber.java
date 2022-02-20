package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.create_order_dtx.command.OrderUpdateForCreateFailedCommand;
import com.mt.saga.appliction.invalid_order_dtx.command.*;
import com.mt.saga.appliction.reserve_order_dtx.command.DecreaseOrderStorageForReserveReplyEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelInvalidOrderDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateInvalidOrderDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InvalidOrderDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;
    @Value("${mt.app.name.mt6}")
    private String paymentAppName;
    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_INVALID_ORDER_DTX_EVENT, (event) -> {
            CreateInvalidOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateInvalidOrderDTXEvent.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT, (event) -> {
            RemoveOrderForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), RemoveOrderForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.RESTORE_CART_FOR_INVALID_EVENT, (event) -> {
            RestoreCartForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), RestoreCartForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyOf(mallAppName, false,AppConstant.INCREASE_STORAGE_FOR_INVALID_EVENT, (event) -> {
            IncreaseStorageForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), IncreaseStorageForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        CommonDomainRegistry.getEventStreamService().replyOf(paymentAppName, false,AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT, (event) -> {
            RemovePaymentQRLinkForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), RemovePaymentQRLinkForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(appName, true,AppConstant.CANCEL_INVALID_ORDER_DTX_SUCCESS_EVENT, (event) -> {
            CancelInvalidOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelInvalidOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_UPDATE_FOR_INVALID_FAILED, (event) -> {
            OrderUpdateForInvalidFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), OrderUpdateForInvalidFailedCommand.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener8() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.RESTORE_CART_FAILED_EVENT, (event) -> {
            RestoreCartForInvalidFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), RestoreCartForInvalidFailedCommand.class);
            ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }
}
