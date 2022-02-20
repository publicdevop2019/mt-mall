package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_conclude_order_dtx.command.CancelDecreaseActualStorageForConcludeReplyEvent;
import com.mt.saga.appliction.cancel_conclude_order_dtx.command.CancelUpdateOrderForConcludeReplyEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelConcludeOrderDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CONCLUDE_ORDER_DTX_FAILED_EVENT,(event) -> {
            ConcludeOrderDTXFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ConcludeOrderDTXFailedEvent.class);
            ApplicationServiceRegistry.getCancelConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(mallAppName, false, AppConstant.DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT, (event) -> {
            CancelDecreaseActualStorageForConcludeReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelDecreaseActualStorageForConcludeReplyEvent.class);
            ApplicationServiceRegistry.getCancelConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profileAppName, false, AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT, (event) -> {
            CancelUpdateOrderForConcludeReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForConcludeReplyEvent.class);
            ApplicationServiceRegistry.getCancelConcludeOrderDTXApplicationService().handle(deserialize);
        });
    }
}