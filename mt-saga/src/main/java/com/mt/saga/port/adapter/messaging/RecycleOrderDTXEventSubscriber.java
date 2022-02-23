package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.recycle_order_dtx.command.IncreaseOrderStorageForRecycleReplyEvent;
import com.mt.saga.appliction.recycle_order_dtx.command.OrderUpdateForRecycleFailedCommand;
import com.mt.saga.appliction.recycle_order_dtx.command.UpdateOrderForRecycleReplyEvent;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelRecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateRecycleOrderDTXEvent;
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
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false,AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT, (event) -> {
            UpdateOrderForRecycleReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForRecycleReplyEvent.class);
            ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyOf(mallAppName, false,AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT, (event) -> {
            IncreaseOrderStorageForRecycleReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), IncreaseOrderStorageForRecycleReplyEvent.class);
            ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(appName, true,AppConstant.CANCEL_RECYCLE_ORDER_DTX_SUCCESS_EVENT, (event) -> {
            CancelRecycleOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelRecycleOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().handle(deserialize);
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
