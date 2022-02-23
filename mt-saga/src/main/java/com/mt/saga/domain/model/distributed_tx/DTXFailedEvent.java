package com.mt.saga.domain.model.distributed_tx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;

@Getter
public class DTXFailedEvent extends DomainEvent {
    public static final String name = "DTX_FAILED_EVENT";
    private DistributedTx distributedTx;
    private DTXFailedEvent(){}
    public DTXFailedEvent(DistributedTx distributedTx,String topic) {
        this.distributedTx = distributedTx;
        setInternal(true);
        setTopic(topic);
        setDomainId(new DomainId(distributedTx.getId().toString()));
        setName(name);
    }
}
