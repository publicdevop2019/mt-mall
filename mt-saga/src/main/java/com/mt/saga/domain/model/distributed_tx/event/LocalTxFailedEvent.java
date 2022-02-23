package com.mt.saga.domain.model.distributed_tx.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Data;

@Data
public class LocalTxFailedEvent extends DomainEvent {
    private long taskId;
}
