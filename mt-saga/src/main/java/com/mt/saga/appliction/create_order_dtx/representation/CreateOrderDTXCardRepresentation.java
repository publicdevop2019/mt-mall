package com.mt.saga.appliction.create_order_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateOrderDTXCardRepresentation extends CommonDTXCardRepresentation {
    public CreateOrderDTXCardRepresentation(CreateOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
    }
}
