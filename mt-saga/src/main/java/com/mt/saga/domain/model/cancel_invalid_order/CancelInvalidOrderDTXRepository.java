package com.mt.saga.domain.model.cancel_invalid_order;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTXQuery;

import java.util.Optional;

public interface CancelInvalidOrderDTXRepository {
    void createOrUpdate(CancelInvalidOrderDTX createOrderTask);

    Optional<CancelInvalidOrderDTX> getById(Long id);

    SumPagedRep<CancelInvalidOrderDTX> query(CancelInvalidOrderDTXQuery var0);
}
