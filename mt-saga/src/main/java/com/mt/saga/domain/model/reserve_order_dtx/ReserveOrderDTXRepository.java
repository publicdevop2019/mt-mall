package com.mt.saga.domain.model.reserve_order_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveOrderDTXRepository {

    void createOrUpdate(ReserveOrderDTX task);

    Optional<ReserveOrderDTX> getById(Long id);

    SumPagedRep<ReserveOrderDTX> query(ReserveOrderDTXQuery var0);
}
