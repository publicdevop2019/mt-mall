package com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command;

import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderPaymentSuccessReplyEvent extends ReplyEvent {
}
