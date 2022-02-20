package com.mt.saga.domain.model.update_order_address_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTXQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpdateOrderAddressDTXRepository {

    void createOrUpdate(UpdateOrderAddressDTX task);

    Optional<UpdateOrderAddressDTX> getById(Long id);

    SumPagedRep<UpdateOrderAddressDTX> query(UpdateOrderAddressDTXQuery var0);
}
