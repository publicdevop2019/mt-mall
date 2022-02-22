package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.DistributedTxRepository;
import com.mt.saga.domain.model.distributed_tx.DistributedTx_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;

public interface SpringDataJpaDistributedTxRepository extends JpaRepository<DistributedTx, Long>, DistributedTxRepository {
    @Override
    default void store(DistributedTx createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<DistributedTx> getById(Long id) {
        return findById(id);
    }

    @Override
    default SumPagedRep<DistributedTx> query(DistributedTxQuery query) {
        return QueryBuilderRegistry.getDistributedTxAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiDistributedTxAdapter {
        public SumPagedRep<DistributedTx> execute(DistributedTxQuery query) {
            QueryUtility.QueryContext<DistributedTx> queryContext = QueryUtility.prepareContext(DistributedTx.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), DistributedTx_.ID, queryContext));
            Optional.ofNullable(query.getNames()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getNames(), DistributedTx_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), DistributedTx_.LOCK_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), DistributedTx_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), DistributedTx_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(DistributedTx_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
