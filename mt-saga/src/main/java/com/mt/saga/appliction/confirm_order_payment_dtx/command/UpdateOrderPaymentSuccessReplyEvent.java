package com.mt.saga.appliction.confirm_order_payment_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderPaymentSuccessReplyEvent extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
}
