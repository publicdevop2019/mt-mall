package com.mt.saga.domain.model.cancel_conclude_order_dtx;

import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancelConcludeOrderDTXRepository {

    void createOrUpdate(CancelConcludeOrderDTX createOrderTask);

    Optional<CancelConcludeOrderDTX> getById(Long id);

    SumPagedRep<CancelConcludeOrderDTX> query(CancelConcludeOrderDTXQuery var0);
}
