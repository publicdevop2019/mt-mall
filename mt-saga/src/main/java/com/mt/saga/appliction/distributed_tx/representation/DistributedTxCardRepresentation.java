package com.mt.saga.appliction.distributed_tx.representation;

import com.mt.saga.domain.model.distributed_tx.DTXStatus;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DistributedTxCardRepresentation {
    protected Long id;
    protected DTXStatus status;
    protected String changeId;
    protected String orderId;
    protected long createdAt;
    protected String name;
    public DistributedTxCardRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        setName(var0.getName());
    }
}
