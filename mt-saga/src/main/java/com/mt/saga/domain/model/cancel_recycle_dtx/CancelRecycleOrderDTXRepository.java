package com.mt.saga.domain.model.cancel_recycle_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancelRecycleOrderDTXRepository {

    void createOrUpdate(CancelRecycleOrderDTX createOrderTask);

    Optional<CancelRecycleOrderDTX> getById(Long id);

    SumPagedRep<CancelRecycleOrderDTX> query(CancelRecycleOrderDTXQuery var0);
}
