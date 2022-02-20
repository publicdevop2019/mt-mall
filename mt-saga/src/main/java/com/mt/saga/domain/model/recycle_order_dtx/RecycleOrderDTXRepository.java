package com.mt.saga.domain.model.recycle_order_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecycleOrderDTXRepository {

    void createOrUpdate(RecycleOrderDTX createOrderTask);

    Optional<RecycleOrderDTX> getById(Long id);

    SumPagedRep<RecycleOrderDTX> query(RecycleOrderDTXQuery var0);
}
