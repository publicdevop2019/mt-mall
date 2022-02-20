package com.mt.saga.domain.model.invalid_order;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTXQuery;

import java.util.Optional;

public interface InvalidOrderDTXRepository {
    void createOrUpdate(InvalidOrderDTX createOrderTask);

    Optional<InvalidOrderDTX> getById(Long id);

    SumPagedRep<InvalidOrderDTX> query(InvalidOrderDTXQuery var0);
}
