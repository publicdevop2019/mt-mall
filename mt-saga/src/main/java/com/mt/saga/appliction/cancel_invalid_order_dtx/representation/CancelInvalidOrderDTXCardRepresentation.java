package com.mt.saga.appliction.cancel_invalid_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelInvalidOrderDTXCardRepresentation  extends CommonDTXCardRepresentation {
    public CancelInvalidOrderDTXCardRepresentation(CancelInvalidOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
    }
}
