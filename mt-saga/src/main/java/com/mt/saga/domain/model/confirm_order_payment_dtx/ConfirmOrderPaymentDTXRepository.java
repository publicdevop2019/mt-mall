package com.mt.saga.domain.model.confirm_order_payment_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmOrderPaymentDTXRepository {

    void createOrUpdate(ConfirmOrderPaymentDTX task);

    Optional<ConfirmOrderPaymentDTX> getById(Long id);

    SumPagedRep<ConfirmOrderPaymentDTX> query(ConfirmOrderPaymentDTXQuery var0);
}
