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
public class CreateRecycleOrderDTXEvent extends DomainEvent {
    private CommonOrderCommand command;
    public static final String name = "CREATE_RECYCLE_ORDER_DTX_EVENT";
    public CreateRecycleOrderDTXEvent(CommonOrderCommand command) {
        this.command = command;
        setInternal(true);
        setTopic(AppConstant.CREATE_RECYCLE_ORDER_DTX_EVENT);
        setName(name);
    }
}
