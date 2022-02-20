package com.mt.saga.appliction.conclude_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConcludeOrderDTXCardRepresentation  extends CommonDTXCardRepresentation {
    public ConcludeOrderDTXCardRepresentation(ConcludeOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());


    }
}

