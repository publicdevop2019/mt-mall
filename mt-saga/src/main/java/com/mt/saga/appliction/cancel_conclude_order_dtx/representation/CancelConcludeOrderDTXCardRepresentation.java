package com.mt.saga.appliction.cancel_conclude_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelConcludeOrderDTXCardRepresentation extends CommonDTXCardRepresentation {
    public String resolveReason;
    public CancelConcludeOrderDTXCardRepresentation(CancelConcludeOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        setResolveReason(var0.getResolveReason());
    }
}
