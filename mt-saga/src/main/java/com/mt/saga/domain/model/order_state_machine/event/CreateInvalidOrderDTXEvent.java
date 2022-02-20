package com.mt.saga.domain.model.order_state_machine.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateInvalidOrderDTXEvent extends DomainEvent {
    public static final String name = "CREATE_INVALID_ORDER_DTX_EVENT";
    private CommonOrderCommand command;

    public CreateInvalidOrderDTXEvent(CommonOrderCommand command) {
        this.command = command;
        setInternal(true);
        setTopic(AppConstant.CREATE_INVALID_ORDER_DTX_EVENT);
        setName(name);
    }
}
