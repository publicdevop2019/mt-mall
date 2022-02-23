package com.mt.saga.appliction.cancel_invalid_order_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelRemoveOrderForInvalidReplyEvent extends ReplyEvent {
}
