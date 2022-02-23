package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelIncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelRemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelRemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelRestoreCartForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import com.mt.saga.domain.model.distributed_tx.event.DistributedTxFailedEvent;
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
            DistributedTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DistributedTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().invalidOrderFailed(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(mallAppName, false, AppConstant.INCREASE_STORAGE_FOR_INVALID_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, CancelIncreaseStorageForInvalidEvent.name);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, CancelRemoveOrderForInvalidEvent.name);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.RESTORE_CART_FOR_INVALID_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, CancelRestoreCartForInvalidEvent.name);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(paymentAppName, false, AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, CancelRemovePaymentQRLinkForInvalidEvent.name);
        });
    }
}
