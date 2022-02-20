package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.create_order_dtx.command.*;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelCreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateCreateOrderDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderDTXEventSubscriber {
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;
    @Value("${mt.app.name.mt6}")
    private String paymentAppName;
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_CREATE_ORDER_DTX_EVENT, (event) -> {
            CreateCreateOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateCreateOrderDTXEvent.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().createCreateOrderTask(deserialize);
        });
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.CLEAR_CART_FOR_CREATE_EVENT, (event) -> {
            ClearCartReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ClearCartReplyCommand.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false,
                AppConstant.SAVE_NEW_ORDER_FOR_CREATE_EVENT,
                (event) -> {
            SaveNewOrderReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), SaveNewOrderReplyCommand.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().replyOf(mallAppName, false, AppConstant.DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT
                , (event) -> {
            DecreaseOrderStorageForCreateReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DecreaseOrderStorageForCreateReplyCommand.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        CommonDomainRegistry.getEventStreamService().replyOf(paymentAppName, false, AppConstant.GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT, (event) -> {
            GeneratePaymentQRLinkReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), GeneratePaymentQRLinkReplyCommand.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CANCEL_CREATE_ORDER_DTX_SUCCESS_EVENT, (event) -> {
            CancelCreateOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelCreateOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.CLEAR_CART_FAILED_EVENT, (event) -> {
            ClearCartFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ClearCartFailedCommand.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener8() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_UPDATE_FOR_CREATE_FAILED, (event) -> {
            OrderUpdateForCreateFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), OrderUpdateForCreateFailedCommand.class);
            ApplicationServiceRegistry.getCreateOrderDTXApplicationService().handle(deserialize);
        });
    }
}
