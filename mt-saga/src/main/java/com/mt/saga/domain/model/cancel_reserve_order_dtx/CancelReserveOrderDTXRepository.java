package com.mt.saga.domain.model.cancel_reserve_order_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface CancelReserveOrderDTXRepository {
    void createOrUpdate(CancelReserveOrderDTX createOrderTask);

    Optional<CancelReserveOrderDTX> getById(Long id);

    SumPagedRep<CancelReserveOrderDTX> query(CancelReserveOrderDTXQuery var0);
}
