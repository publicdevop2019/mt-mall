package com.mt.saga.appliction.distributed_tx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Data;

@Data
public class LocalTxFailedEvent extends DomainEvent {
    private long taskId;
}
