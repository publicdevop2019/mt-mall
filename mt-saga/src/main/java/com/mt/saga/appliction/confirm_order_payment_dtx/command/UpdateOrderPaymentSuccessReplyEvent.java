package com.mt.saga.appliction.confirm_order_payment_dtx.command;

import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderPaymentSuccessReplyEvent extends ReplyEvent {
}
