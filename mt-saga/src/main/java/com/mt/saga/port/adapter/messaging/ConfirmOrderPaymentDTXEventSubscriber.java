package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.domain.model.distributed_tx.event.confirm_order_payment_dtx.UpdateOrderPaymentSuccessEvent;
import com.mt.saga.appliction.distributed_tx.command.ReplyEvent;
import com.mt.saga.appliction.distributed_tx.command.LocalTxFailedEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateConfirmOrderPaymentDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConfirmOrderPaymentDTXEventSubscriber {
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${spring.application.name}")
    private String appName;



    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_UPDATE_FOR_PAYMENT_SUCCESS_FAILED, (event) -> {
            LocalTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), LocalTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().cancel(deserialize.getTaskId());
        });
    }
}
