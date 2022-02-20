package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.reserve_order_dtx.command.DecreaseOrderStorageForReserveReplyEvent;
import com.mt.saga.appliction.reserve_order_dtx.command.OrderUpdateForReserveFailedCommand;
import com.mt.saga.appliction.reserve_order_dtx.command.UpdateOrderForReserveReplyEvent;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateReserveOrderDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReserveOrderDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_RESERVE_ORDER_DTX_EVENT, (event) -> {
            CreateReserveOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateReserveOrderDTXEvent.class);
            ApplicationServiceRegistry.getReserveOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_RESERVE_EVENT, (event) -> {
            UpdateOrderForReserveReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForReserveReplyEvent.class);
            ApplicationServiceRegistry.getReserveOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyOf(mallAppName, false,AppConstant.DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT, (event) -> {
            DecreaseOrderStorageForReserveReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DecreaseOrderStorageForReserveReplyEvent.class);
            ApplicationServiceRegistry.getReserveOrderDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(appName, true,AppConstant.CANCEL_RESERVE_ORDER_DTX_SUCCESS_EVENT, (event) -> {
            CancelReserveOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelReserveOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getReserveOrderDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false,AppConstant.ORDER_UPDATE_FOR_RESERVE_FAILED, (event) -> {
            OrderUpdateForReserveFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), OrderUpdateForReserveFailedCommand.class);
            ApplicationServiceRegistry.getReserveOrderDTXApplicationService().handle(deserialize);
        });
    }
}
