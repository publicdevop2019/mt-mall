package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_reserve_order_dtx.command.CancelDecreaseOrderStorageForReserveReplyEvent;
import com.mt.saga.appliction.cancel_reserve_order_dtx.command.CancelUpdateOrderForReserveReplyEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelReserveOrderDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.RESERVE_ORDER_DTX_FAILED_EVENT, (event) -> {
            DTXFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DTXFailedEvent.class);
            ApplicationServiceRegistry.getCancelReserveOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(mallAppName, false, AppConstant.DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT, (event) -> {
            CancelDecreaseOrderStorageForReserveReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelDecreaseOrderStorageForReserveReplyEvent.class);
            ApplicationServiceRegistry.getCancelReserveOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_RESERVE_EVENT, (event) -> {
            CancelUpdateOrderForReserveReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForReserveReplyEvent.class);
            ApplicationServiceRegistry.getCancelReserveOrderDTXApplicationService().handle(deserialize);
        });
    }
}
