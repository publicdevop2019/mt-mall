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
public class CreateCreateOrderDTXEvent extends DomainEvent {
    public static final String name = "CREATE_CREATE_ORDER_DTX_EVENT";
    private CommonOrderCommand command;

    public CreateCreateOrderDTXEvent(CommonOrderCommand command) {
        this.command = command;
        setInternal(true);
        setTopic(AppConstant.CREATE_CREATE_ORDER_DTX_EVENT);
        setName(name);
    }
}
