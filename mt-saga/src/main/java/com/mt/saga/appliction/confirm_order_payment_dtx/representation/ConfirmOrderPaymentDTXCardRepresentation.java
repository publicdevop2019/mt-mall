package com.mt.saga.appliction.confirm_order_payment_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfirmOrderPaymentDTXCardRepresentation  extends CommonDTXCardRepresentation {
    public ConfirmOrderPaymentDTXCardRepresentation(ConfirmOrderPaymentDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());


    }
}

