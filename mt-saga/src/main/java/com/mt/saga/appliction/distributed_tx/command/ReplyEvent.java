package com.mt.saga.appliction.distributed_tx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;

@Getter
public class ReplyEvent extends DomainEvent {
    private  Long taskId;
    private  boolean emptyOpt;
    private  String replyOf;
}
