package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTXQuery;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTXRepository;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCancelReserveOrderDTXRepository extends JpaRepository<CancelReserveOrderDTX, Long>, CancelReserveOrderDTXRepository {

    @Override
    default void createOrUpdate(CancelReserveOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CancelReserveOrderDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<CancelReserveOrderDTX> query(CancelReserveOrderDTXQuery query) {
        return QueryBuilderRegistry.getCancelReserveOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCancelReserveOrderDTXAdapter {
        public SumPagedRep<CancelReserveOrderDTX> execute(CancelReserveOrderDTXQuery query) {
            QueryUtility.QueryContext<CancelReserveOrderDTX> queryContext = QueryUtility.prepareContext(CancelReserveOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CancelReserveOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CancelReserveOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CancelReserveOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CancelReserveOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CancelReserveOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
