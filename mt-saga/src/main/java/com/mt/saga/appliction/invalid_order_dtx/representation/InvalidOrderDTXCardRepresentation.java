package com.mt.saga.appliction.invalid_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvalidOrderDTXCardRepresentation extends CommonDTXCardRepresentation {
    public InvalidOrderDTXCardRepresentation(InvalidOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
    }
}
