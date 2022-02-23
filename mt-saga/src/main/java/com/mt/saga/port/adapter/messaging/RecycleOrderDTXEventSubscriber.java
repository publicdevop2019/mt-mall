package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.recycle_order_dtx.command.OrderUpdateForRecycleFailedCommand;
import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateRecycleOrderDTXEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.IncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.UpdateOrderForRecycleEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RecycleOrderDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_RECYCLE_ORDER_DTX_EVENT, (event) -> {
            CreateRecycleOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateRecycleOrderDTXEvent.class);
            ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, UpdateOrderForRecycleEvent.name);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyOf(mallAppName, false, AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, IncreaseOrderStorageForRecycleEvent.name);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_UPDATE_FOR_RECYCLE_FAILED, (event) -> {
            OrderUpdateForRecycleFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), OrderUpdateForRecycleFailedCommand.class);
            ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().handle(deserialize);
        });
    }
}
