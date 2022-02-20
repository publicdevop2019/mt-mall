package com.mt.saga.appliction.cancel_recycle_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelRecycleOrderDTXCardRepresentation  extends CommonDTXCardRepresentation {
    public String resolveReason;
    public CancelRecycleOrderDTXCardRepresentation(CancelRecycleOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        setResolveReason(var0.getResolveReason());

    }
}

