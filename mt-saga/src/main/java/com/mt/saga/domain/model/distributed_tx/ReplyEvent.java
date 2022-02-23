package com.mt.saga.domain.model.distributed_tx;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;

@Getter
public abstract class ReplyEvent extends DomainEvent {
    private  Long taskId;
    private  boolean emptyOpt;
}
