package com.mt.saga.domain.model.conclude_order_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcludeOrderDTXRepository {

    void createOrUpdate(ConcludeOrderDTX createOrderTask);

    Optional<ConcludeOrderDTX> getById(Long id);

    SumPagedRep<ConcludeOrderDTX> query(ConcludeOrderDTXQuery var0);
}
