package com.mt.saga.domain.model.create_order_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface CreateOrderDTXRepository {

    void createOrUpdate(CreateOrderDTX createOrderTask);

    Optional<CreateOrderDTX> getById(Long id);

    SumPagedRep<CreateOrderDTX> query(CreateOrderDTXQuery var0);
}
