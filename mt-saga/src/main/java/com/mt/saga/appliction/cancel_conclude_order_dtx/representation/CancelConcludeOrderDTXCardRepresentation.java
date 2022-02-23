package com.mt.saga.appliction.cancel_conclude_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelConcludeOrderDTXCardRepresentation extends CommonDTXCardRepresentation {
    public String resolveReason;
    public CancelConcludeOrderDTXCardRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        setResolveReason(var0.getResolveReason());
    }
}
