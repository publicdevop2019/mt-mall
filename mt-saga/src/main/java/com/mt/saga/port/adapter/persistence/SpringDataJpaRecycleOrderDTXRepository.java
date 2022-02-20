package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX_;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTXQuery;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTXRepository;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaRecycleOrderDTXRepository extends JpaRepository<RecycleOrderDTX, Long>, RecycleOrderDTXRepository {

    @Override
    default void createOrUpdate(RecycleOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<RecycleOrderDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<RecycleOrderDTX> query(RecycleOrderDTXQuery query) {
        return QueryBuilderRegistry.getRecycleOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiRecycleOrderDTXAdapter {
        public SumPagedRep<RecycleOrderDTX> execute(RecycleOrderDTXQuery query) {
            QueryUtility.QueryContext<RecycleOrderDTX> queryContext = QueryUtility.prepareContext(RecycleOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), RecycleOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), RecycleOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), RecycleOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), RecycleOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(RecycleOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

}
