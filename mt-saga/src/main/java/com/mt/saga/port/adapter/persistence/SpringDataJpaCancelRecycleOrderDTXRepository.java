package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX_;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTXQuery;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTXRepository;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX_;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTXRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCancelRecycleOrderDTXRepository extends JpaRepository<CancelRecycleOrderDTX, Long>, CancelRecycleOrderDTXRepository {

    @Override
    default void createOrUpdate(CancelRecycleOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CancelRecycleOrderDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<CancelRecycleOrderDTX> query(CancelRecycleOrderDTXQuery query) {
        return QueryBuilderRegistry.getCancelRecycleOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCancelRecycleOrderDTXAdapter {
        public SumPagedRep<CancelRecycleOrderDTX> execute(CancelRecycleOrderDTXQuery query) {
            QueryUtility.QueryContext<CancelRecycleOrderDTX> queryContext = QueryUtility.prepareContext(CancelRecycleOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CancelRecycleOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CancelRecycleOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CancelRecycleOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CancelRecycleOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CancelRecycleOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
