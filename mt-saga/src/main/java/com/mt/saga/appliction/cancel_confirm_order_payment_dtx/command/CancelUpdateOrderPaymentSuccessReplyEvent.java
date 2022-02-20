package com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderPaymentSuccessReplyEvent extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
}
