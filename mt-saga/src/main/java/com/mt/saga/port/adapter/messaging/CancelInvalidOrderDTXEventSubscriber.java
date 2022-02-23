package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_invalid_order_dtx.command.CancelIncreaseStorageForInvalidReplyEvent;
import com.mt.saga.appliction.cancel_invalid_order_dtx.command.CancelRemoveOrderForInvalidReplyEvent;
import com.mt.saga.appliction.cancel_invalid_order_dtx.command.CancelRemovePaymentQRLinkForInvalidReplyEvent;
import com.mt.saga.appliction.cancel_invalid_order_dtx.command.CancelRestoreCartForInvalidReplyEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelInvalidOrderDTXEventSubscriber {
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
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.INVALID_ORDER_DTX_FAILED_EVENT, (event) -> {
            DTXFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DTXFailedEvent.class);
            ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(mallAppName, false, AppConstant.INCREASE_STORAGE_FOR_INVALID_EVENT, (event) -> {
            CancelIncreaseStorageForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelIncreaseStorageForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT, (event) -> {
            CancelRemoveOrderForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelRemoveOrderForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.RESTORE_CART_FOR_INVALID_EVENT, (event) -> {
            CancelRestoreCartForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelRestoreCartForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(paymentAppName, false, AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT, (event) -> {
            CancelRemovePaymentQRLinkForInvalidReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelRemovePaymentQRLinkForInvalidReplyEvent.class);
            ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().handle(deserialize);
        });
    }
}
