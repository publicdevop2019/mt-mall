package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.conclude_order_dtx.command.DecreaseActualStorageForConcludeReplyEvent;
import com.mt.saga.appliction.conclude_order_dtx.command.OrderUpdateForConcludeFailedCommand;
import com.mt.saga.appliction.conclude_order_dtx.command.UpdateOrderForConcludeReplyEvent;
import com.mt.saga.appliction.reserve_order_dtx.command.OrderUpdateForReserveFailedCommand;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateConcludeOrderDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConcludeOrderDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_CONCLUDE_ORDER_DTX_EVENT, (event) -> {
            CreateConcludeOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateConcludeOrderDTXEvent.class);
            ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT, (event) -> {
            UpdateOrderForConcludeReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForConcludeReplyEvent.class);
            ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyOf(mallAppName, false, AppConstant.DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT, (event) -> {
            DecreaseActualStorageForConcludeReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DecreaseActualStorageForConcludeReplyEvent.class);
            ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CANCEL_CONCLUDE_ORDER_DTX_SUCCESS_EVENT, (event) -> {
            CancelConcludeOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelConcludeOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false,AppConstant.ORDER_UPDATE_FOR_CONCLUDE_FAILED, (event) -> {
            OrderUpdateForConcludeFailedCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), OrderUpdateForConcludeFailedCommand.class);
            ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }
}
