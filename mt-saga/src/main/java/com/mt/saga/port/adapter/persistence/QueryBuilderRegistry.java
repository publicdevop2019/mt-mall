package com.mt.saga.port.adapter.persistence;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaDistributedTxRepository.JpaCriteriaApiDistributedTxAdapter distributedTxAdapter;

    @Autowired
    public void setDistributedTxAdapter(SpringDataJpaDistributedTxRepository.JpaCriteriaApiDistributedTxAdapter distributedTxAdapter) {
        QueryBuilderRegistry.distributedTxAdapter = distributedTxAdapter;
    }
}
