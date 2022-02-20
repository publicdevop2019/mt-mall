package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelIncreaseOrderStorageForRecycleReplyEvent;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelUpdateOrderForRecycleReplyEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelRecycleOrderDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.RECYCLE_ORDER_DTX_FAILED_EVENT, (event) -> {
            RecycleOrderDTXFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), RecycleOrderDTXFailedEvent.class);
            ApplicationServiceRegistry.getCancelRecycleOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT, (event) -> {
            CancelUpdateOrderForRecycleReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForRecycleReplyEvent.class);
            ApplicationServiceRegistry.getCancelRecycleOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(mallAppName, false, AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT, (event) -> {
            CancelIncreaseOrderStorageForRecycleReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelIncreaseOrderStorageForRecycleReplyEvent.class);
            ApplicationServiceRegistry.getCancelRecycleOrderDTXApplicationService().handle(deserialize);
        });
    }
}
