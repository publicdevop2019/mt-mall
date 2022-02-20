package com.mt.saga.domain.model.cancel_confirm_order_payment_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface CancelConfirmOrderPaymentDTXRepository {
    void createOrUpdate(CancelConfirmOrderPaymentDTX createOrderTask);

    Optional<CancelConfirmOrderPaymentDTX> getById(Long id);

    SumPagedRep<CancelConfirmOrderPaymentDTX> query(CancelConfirmOrderPaymentDTXQuery var0);
}
