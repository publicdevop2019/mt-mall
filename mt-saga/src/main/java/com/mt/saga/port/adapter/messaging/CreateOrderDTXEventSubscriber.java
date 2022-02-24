package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.distributed_tx.command.DistributedTxFailedEvent;
import com.mt.saga.appliction.distributed_tx.command.DistributedTxSuccessEvent;
import com.mt.saga.appliction.distributed_tx.command.LocalTxFailedEvent;
import com.mt.saga.appliction.distributed_tx.command.ReplyEvent;
import com.mt.saga.appliction.distributed_tx.command.create_order_dtx.GeneratePaymentQRLinkReplyCommand;
import com.mt.saga.domain.model.distributed_tx.event.create_order_dtx.GeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.order_state_machine.event.*;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderDTXEventSubscriber {
    @Value("${mt.app.name.mt2}")
    private String profileAppName;
    @Value("${mt.app.name.mt3}")
    private String mallAppName;
    @Value("${mt.app.name.mt6}")
    private String paymentAppName;
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener11() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT, (event) -> {
            CreateConfirmOrderPaymentDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateConfirmOrderPaymentDTXEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener15() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_UPDATE_ORDER_ADDRESS_DTX_EVENT, (event) -> {
            CreateUpdateOrderAddressDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateUpdateOrderAddressDTXEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener14() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_RESERVE_ORDER_DTX_EVENT, (event) -> {
            CreateReserveOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateReserveOrderDTXEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener13() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_RECYCLE_ORDER_DTX_EVENT, (event) -> {
            CreateRecycleOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateRecycleOrderDTXEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener12() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_INVALID_ORDER_DTX_EVENT, (event) -> {
            CreateInvalidOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateInvalidOrderDTXEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener10() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_CONCLUDE_ORDER_DTX_EVENT, (event) -> {
            CreateConcludeOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateConcludeOrderDTXEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener9() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CREATE_CREATE_ORDER_DTX_EVENT, (event) -> {
            CreateCreateOrderDTXEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateCreateOrderDTXEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().createCreateOrderTask(deserialize);
        });
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        CommonDomainRegistry.getEventStreamService().of("*", false, AppConstant.SAGA_REPLY_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            log.debug("handing saga reply event of {}", deserialize.getReplyOf());
            if (deserialize.getReplyOf().equals(GeneratePaymentQRLinkEvent.name)) {
                GeneratePaymentQRLinkReplyCommand deserialize2 = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), GeneratePaymentQRLinkReplyCommand.class);
                ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize2);
            } else {
                ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, deserialize.getReplyOf());
            }
        });
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.DTX_SUCCESS_EVENT, (event) -> {
            DistributedTxSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DistributedTxSuccessEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.CLEAR_CART_FAILED_EVENT, (event) -> {
            LocalTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), LocalTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener8() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_UPDATE_FOR_CREATE_FAILED, (event) -> {
            LocalTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), LocalTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.DTX_FAILED_EVENT, (event) -> {
            DistributedTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DistributedTxFailedEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize);
        });
    }
}
