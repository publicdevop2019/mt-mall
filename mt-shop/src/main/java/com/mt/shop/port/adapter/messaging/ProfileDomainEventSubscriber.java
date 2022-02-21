package com.mt.shop.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.biz_order.command.*;
import com.mt.shop.application.cart.command.CancelClearCartForCreateCommand;
import com.mt.shop.application.cart.command.CancelRestoreCartForInvalidEvent;
import com.mt.shop.application.cart.command.ClearCartForCreateEvent;
import com.mt.shop.application.cart.command.RestoreCartForInvalidEvent;
import com.mt.shop.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProfileDomainEventSubscriber {
    @Value("${mt.app.name.mt15}")
    private String sagaAppName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.SAVE_NEW_ORDER_EVENT, (event) -> {
            InternalCreateNewOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), InternalCreateNewOrderCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.SAVE_NEW_ORDER_EVENT, (event) -> {
            InternalCancelNewOrderEvent command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), InternalCancelNewOrderEvent.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.CLEAR_CART_EVENT, (event) -> {
            ClearCartForCreateEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ClearCartForCreateEvent.class);
            ApplicationServiceRegistry.getCartApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.CLEAR_CART_EVENT, (event) -> {
            CancelClearCartForCreateCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelClearCartForCreateCommand.class);
            ApplicationServiceRegistry.getCartApplicationService().cancelClearCart(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT, (event) -> {
            UpdateOrderForRecycleCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForRecycleCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_RESERVE_EVENT, (event) -> {
            UpdateOrderForReserveCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForReserveCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.UPDATE_ORDER_PAYMENT_SUCCESS_EVENT, (event) -> {
            UpdateOrderPaymentSuccessCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderPaymentSuccessCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT, (event) -> {
            UpdateOrderForConcludeCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForConcludeCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener8() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_RESERVE_EVENT, (event) -> {
            CancelUpdateOrderForReserveCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForReserveCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener9() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT, (event) -> {
            CancelUpdateOrderForRecycleCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForRecycleCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener10() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT, (event) -> {
            CancelUpdateOrderForConcludeCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForConcludeCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener11() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.UPDATE_ORDER_PAYMENT_SUCCESS_EVENT, (event) -> {
            CancelUpdateOrderPaymentSuccessCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderPaymentSuccessCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener12() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT, (event) -> {
            UpdateOrderForUpdateAddressCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForUpdateAddressCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener13() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT, (event) -> {
            CancelUpdateOrderForUpdateOrderAddressCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForUpdateOrderAddressCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener14() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT, (event) -> {
            UpdateOrderForInvalidCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UpdateOrderForInvalidCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener15() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT, (event) -> {
            CancelUpdateOrderForInvalidCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelUpdateOrderForInvalidCommand.class);
            ApplicationServiceRegistry.getBizOrderApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener16() {
        CommonDomainRegistry.getEventStreamService().of(sagaAppName, false, AppConstant.RESTORE_CART_FOR_INVALID_EVENT, (event) -> {
            RestoreCartForInvalidEvent command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), RestoreCartForInvalidEvent.class);
            ApplicationServiceRegistry.getCartApplicationService().handle(command);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener17() {
        CommonDomainRegistry.getEventStreamService().cancelOf(sagaAppName, false, AppConstant.RESTORE_CART_FOR_INVALID_EVENT, (event) -> {
            CancelRestoreCartForInvalidEvent command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CancelRestoreCartForInvalidEvent.class);
            ApplicationServiceRegistry.getCartApplicationService().handle(command);
        });
    }

}
