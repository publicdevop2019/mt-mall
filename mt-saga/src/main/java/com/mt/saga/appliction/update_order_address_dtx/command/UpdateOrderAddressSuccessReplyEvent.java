package com.mt.saga.appliction.update_order_address_dtx.command;

import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderAddressSuccessReplyEvent extends ReplyEvent {
}
