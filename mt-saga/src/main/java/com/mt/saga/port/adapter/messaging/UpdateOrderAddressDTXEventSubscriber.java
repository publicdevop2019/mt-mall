package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.update_order_address_dtx.command.OrderUpdateForAddressUpdateFailedCommand;
import com.mt.saga.appliction.update_order_address_dtx.command.UpdateOrderAddressSuccessReplyEvent;
import com.mt.saga.domain.model.distributed_tx.DTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateUpdateOrderAddressDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateOrderAddressDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_UPDATE_ORDER_ADDRESS_DTX_EVENT, (event) -> {
            CreateUpdateOrderAddressDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateUpdateOrderAddressDTXEvent.class);
            ApplicationServiceRegistry.getUpdateOrderAddressDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT, (event) -> {
            UpdateOrderAddressSuccessReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderAddressSuccessReplyEvent.class);
            ApplicationServiceRegistry.getUpdateOrderAddressDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CANCEL_UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT, (event) -> {
            DTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DTXSuccessEvent.class);
            ApplicationServiceRegistry.getUpdateOrderAddressDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_UPDATE_FOR_ADDRESS_UPDATE_FAILED, (event) -> {
            OrderUpdateForAddressUpdateFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), OrderUpdateForAddressUpdateFailedCommand.class);
            ApplicationServiceRegistry.getUpdateOrderAddressDTXApplicationService().handle(deserialize);
        });
    }
}
