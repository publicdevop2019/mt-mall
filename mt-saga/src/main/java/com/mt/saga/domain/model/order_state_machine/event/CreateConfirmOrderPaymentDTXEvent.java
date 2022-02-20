package com.mt.saga.domain.model.order_state_machine.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateConfirmOrderPaymentDTXEvent extends DomainEvent {
    private CommonOrderCommand command;
    private BizOrderStatus status;
    public static final String name = "CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT";
    public CreateConfirmOrderPaymentDTXEvent(CommonOrderCommand command, BizOrderStatus status) {
        this.command = command;
        setInternal(true);
        setTopic(AppConstant.CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT);
        this.status = status;
        setName(name);
    }
}
