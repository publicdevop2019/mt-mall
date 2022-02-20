package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelCreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelInvalidOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelRecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderAddressDTXSuccessEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.create_order_dtx.event.CreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.invalid_order.event.InvalidOrderDTXSuccessEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.ReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderAddressDTXSuccessEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profileAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(profileAppName, false, AppConstant.ORDER_OPERATION_EVENT, (event) -> {
            CommonOrderCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CommonOrderCommand.class);
            log.debug("event detail {}", deserialize);
            ApplicationServiceRegistry.getStateMachineApplicationService().start(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT, (event) -> {
            ConfirmOrderPaymentDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ConfirmOrderPaymentDTXSuccessEvent.class);
            ApplicationServiceRegistry.getStateMachineApplicationService().start(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CANCEL_CONCLUDE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            CancelConcludeOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelConcludeOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CANCEL_CONCLUDE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CANCEL_CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT), (event) -> {
            CancelConfirmOrderPaymentDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelConfirmOrderPaymentDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CANCEL_CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CANCEL_CREATE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            CancelCreateOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelCreateOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CANCEL_CREATE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CANCEL_RECYCLE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            CancelRecycleOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelRecycleOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CANCEL_RECYCLE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CANCEL_RESERVE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            CancelReserveOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelReserveOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CANCEL_RESERVE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CONCLUDE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            ConcludeOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ConcludeOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CONCLUDE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener8() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT), (event) -> {
            ConfirmOrderPaymentDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ConfirmOrderPaymentDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener9() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CREATE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            CreateOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CreateOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CREATE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener10() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.RECYCLE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            RecycleOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), RecycleOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.RECYCLE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener11() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.RESERVE_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            ReserveOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReserveOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.RESERVE_ORDER_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener12() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT), (event) -> {
            UpdateOrderAddressDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderAddressDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener13() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CANCEL_UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT), (event) -> {
            CancelUpdateOrderAddressDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderAddressDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CANCEL_UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT);
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener14() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.INVALID_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            InvalidOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), InvalidOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.INVALID_ORDER_DTX_SUCCESS_EVENT);
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener15() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, getValidatorQueueName(AppConstant.CANCEL_INVALID_ORDER_DTX_SUCCESS_EVENT), (event) -> {
            CancelInvalidOrderDTXSuccessEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelInvalidOrderDTXSuccessEvent.class);
            ApplicationServiceRegistry.getPostDtxValidationApplicationService().handle(deserialize);
        }, AppConstant.CANCEL_INVALID_ORDER_DTX_SUCCESS_EVENT);
    }


    private String getValidatorQueueName(String eventName) {
        return "post_" + eventName + "_validation_handler";
    }


}
