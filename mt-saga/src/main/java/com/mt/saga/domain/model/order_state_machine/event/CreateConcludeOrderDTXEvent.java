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
public class CreateConcludeOrderDTXEvent extends DomainEvent {
    private CommonOrderCommand command;
    public static final String name = "CREATE_CONCLUDE_ORDER_DTX_EVENT";
    public CreateConcludeOrderDTXEvent(CommonOrderCommand command) {
        this.command = command;
        setInternal(true);
        setTopic(AppConstant.CREATE_CONCLUDE_ORDER_DTX_EVENT);
        setName(name);
    }
}
