package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.domain.model.distributed_tx.event.cancel_recycle_dtx.CancelIncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_recycle_dtx.CancelUpdateOrderForRecycleEvent;
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
            DistributedTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DistributedTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().recycleOrderFailed(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, CancelUpdateOrderForRecycleEvent.name);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(mallAppName, false, AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, CancelIncreaseOrderStorageForRecycleEvent.name);
        });
    }
}
