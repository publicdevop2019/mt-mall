package com.mt.saga.domain.model.distributed_tx;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface DistributedTxRepository {
    Optional<DistributedTx> getById(Long id);
    void store(DistributedTx distributedTx);
    SumPagedRep<DistributedTx> query(DistributedTxQuery query);
}
