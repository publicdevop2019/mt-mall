package com.mt.saga.appliction.cancel_recycle_order_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelIncreaseOrderStorageForRecycleReplyEvent extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
}
