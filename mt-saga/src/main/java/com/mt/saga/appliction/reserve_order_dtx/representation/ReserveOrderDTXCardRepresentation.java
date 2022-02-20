package com.mt.saga.appliction.reserve_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReserveOrderDTXCardRepresentation  extends CommonDTXCardRepresentation {
    public ReserveOrderDTXCardRepresentation(ReserveOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());


    }
}

