package com.mt.saga.appliction.confirm_order_payment_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderUpdateForPaymentSuccessFailedCommand extends DomainEvent {
    private long taskId;
}
