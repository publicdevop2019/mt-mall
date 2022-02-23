package com.mt.saga.appliction.cancel_reserve_order_dtx.command;

import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelUpdateOrderForReserveReplyEvent extends ReplyEvent {
}
