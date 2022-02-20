package com.mt.saga.appliction.cancel_reserve_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelReserveOrderDTXCardRepresentation extends CommonDTXCardRepresentation {
    public String resolveReason;
    public CancelReserveOrderDTXCardRepresentation(CancelReserveOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        setResolveReason(var0.getResolveReason());

    }
}

