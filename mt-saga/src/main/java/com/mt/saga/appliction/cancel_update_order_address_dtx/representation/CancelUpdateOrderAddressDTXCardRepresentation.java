package com.mt.saga.appliction.cancel_update_order_address_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelUpdateOrderAddressDTXCardRepresentation extends CommonDTXCardRepresentation {
    public String resolveReason;

    public CancelUpdateOrderAddressDTXCardRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        setResolveReason(var0.getResolveReason());

    }
}
