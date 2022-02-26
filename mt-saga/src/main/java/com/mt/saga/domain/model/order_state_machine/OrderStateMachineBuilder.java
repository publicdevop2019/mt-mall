package com.mt.saga.domain.model.order_state_machine;

import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;

public interface OrderStateMachineBuilder {
    void handle(CommonOrderCommand command);
}
