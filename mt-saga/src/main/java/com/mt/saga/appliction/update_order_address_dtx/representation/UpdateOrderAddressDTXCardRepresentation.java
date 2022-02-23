package com.mt.saga.appliction.update_order_address_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateOrderAddressDTXCardRepresentation extends CommonDTXCardRepresentation {
    public UpdateOrderAddressDTXCardRepresentation(UpdateOrderAddressDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
    }
}

