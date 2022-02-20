package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX_;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTXQuery;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTXRepository;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaReserveOrderDTXRepository extends JpaRepository<ReserveOrderDTX, Long>, ReserveOrderDTXRepository {

    @Override
    default void createOrUpdate(ReserveOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<ReserveOrderDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<ReserveOrderDTX> query(ReserveOrderDTXQuery query) {
        return QueryBuilderRegistry.getReserveOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiReserveOrderDTXAdapter {
        public SumPagedRep<ReserveOrderDTX> execute(ReserveOrderDTXQuery query) {
            QueryUtility.QueryContext<ReserveOrderDTX> queryContext = QueryUtility.prepareContext(ReserveOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), ReserveOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), ReserveOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), ReserveOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), ReserveOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(ReserveOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

}
