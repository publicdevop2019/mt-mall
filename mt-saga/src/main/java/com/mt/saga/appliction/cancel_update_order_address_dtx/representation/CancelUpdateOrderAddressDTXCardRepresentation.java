package com.mt.saga.appliction.cancel_update_order_address_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelUpdateOrderAddressDTXCardRepresentation extends CommonDTXCardRepresentation {
    public String resolveReason;
    public CancelUpdateOrderAddressDTXCardRepresentation(CancelUpdateOrderAddressDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        setResolveReason(var0.getResolveReason());

    }
}
