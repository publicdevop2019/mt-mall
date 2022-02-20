package com.mt.saga.appliction.invalid_order_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestoreCartForInvalidReplyEvent extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
}