package com.mt.saga.domain.model.order_state_machine;

import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXSuccessEvent;

public interface OrderStateMachineBuilder {
    void handle(CommonOrderCommand command);

    void handle(ConfirmOrderPaymentDTXSuccessEvent deserialize);
}
