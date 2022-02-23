package com.mt.saga.domain.model.cancel_update_order_address_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface CancelUpdateOrderAddressDTXRepository {
    void createOrUpdate(CancelUpdateOrderAddressDTX createOrderTask);

    Optional<CancelUpdateOrderAddressDTX> getById(Long id);

    SumPagedRep<CancelUpdateOrderAddressDTX> query(CancelUpdateOrderAddressDTXQuery var0);
}
