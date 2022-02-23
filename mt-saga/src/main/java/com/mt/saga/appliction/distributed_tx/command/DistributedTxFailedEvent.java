package com.mt.saga.appliction.distributed_tx.command;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;

@Getter
public class DistributedTxFailedEvent extends DomainEvent {
    public static final String name = "DTX_FAILED_EVENT";
    private DistributedTx distributedTx;
    private DistributedTxFailedEvent(){}
    public DistributedTxFailedEvent(DistributedTx distributedTx, String topic) {
        this.distributedTx = distributedTx;
        setInternal(true);
        setTopic(topic);
        setDomainId(new DomainId(distributedTx.getId().toString()));
        setName(name);
    }
}
