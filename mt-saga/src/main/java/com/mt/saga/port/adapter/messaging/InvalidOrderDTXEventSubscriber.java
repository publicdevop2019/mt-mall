package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.distributed_tx.command.ReplyEvent;
import com.mt.saga.appliction.distributed_tx.command.LocalTxFailedEvent;
import com.mt.saga.domain.model.distributed_tx.event.invalid_order.IncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.invalid_order.RemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.invalid_order.RemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.invalid_order.RestoreCartForInvalidEvent;
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
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_UPDATE_FOR_INVALID_FAILED, (event) -> {
            LocalTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), LocalTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener8() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.RESTORE_CART_FAILED_EVENT, (event) -> {
            LocalTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), LocalTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }
}
