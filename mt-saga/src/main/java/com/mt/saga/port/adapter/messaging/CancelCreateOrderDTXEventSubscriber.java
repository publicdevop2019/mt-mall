package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelClearCartReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelDecreaseOrderStorageForCreateReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelGeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelSaveNewOrderReplyCommand;
import com.mt.saga.domain.model.create_order_dtx.event.CreateOrderDTXFailedEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelCreateOrderDTXEventSubscriber {

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
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_ORDER_DTX_FAILED_EVENT, (event) -> {
            DTXFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DTXFailedEvent.class);
            ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().handleCommand(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(paymentAppName, false, AppConstant.GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT, (event) -> {
            CancelGeneratePaymentQRLinkReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelGeneratePaymentQRLinkReplyCommand.class);
            ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().handleCommand(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(mallAppName, false, AppConstant.DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT, (event) -> {
            CancelDecreaseOrderStorageForCreateReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelDecreaseOrderStorageForCreateReplyCommand.class);
            ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().handleCommand(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.CLEAR_CART_FOR_CREATE_EVENT, (event) -> {
            CancelClearCartReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelClearCartReplyCommand.class);
            ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().handleCommand(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.SAVE_NEW_ORDER_FOR_CREATE_EVENT, (event) -> {
            CancelSaveNewOrderReplyCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelSaveNewOrderReplyCommand.class);
            ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().handleCommand(deserialize);
        });
    }

}
