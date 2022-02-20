package com.mt.saga.appliction.recycle_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecycleOrderDTXCardRepresentation  extends CommonDTXCardRepresentation {
    public RecycleOrderDTXCardRepresentation(RecycleOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());


    }
}

