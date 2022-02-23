package com.mt.saga.appliction.confirm_order_payment_dtx.representation;

import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfirmOrderPaymentDTXCardRepresentation  extends CommonDTXCardRepresentation {
    public ConfirmOrderPaymentDTXCardRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());


    }
}

