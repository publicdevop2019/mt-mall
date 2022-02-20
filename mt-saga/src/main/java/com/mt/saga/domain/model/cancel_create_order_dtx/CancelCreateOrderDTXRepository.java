package com.mt.saga.domain.model.cancel_create_order_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface CancelCreateOrderDTXRepository {
    void createOrUpdate(CancelCreateOrderDTX createOrderTask);

    Optional<CancelCreateOrderDTX> getById(Long id);

    SumPagedRep<CancelCreateOrderDTX> query(CancelCreateOrderDTXQuery var0);
}
