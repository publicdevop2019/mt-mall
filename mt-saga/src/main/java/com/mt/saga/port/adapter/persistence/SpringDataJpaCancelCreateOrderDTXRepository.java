package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX_;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTXQuery;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTXRepository;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCancelCreateOrderDTXRepository extends JpaRepository<CancelCreateOrderDTX, Long>, CancelCreateOrderDTXRepository {

    @Override
    default void createOrUpdate(CancelCreateOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CancelCreateOrderDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<CancelCreateOrderDTX> query(CancelCreateOrderDTXQuery query) {
        return QueryBuilderRegistry.getCancelCreateOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCancelCreateOrderDTXAdapter {
        public SumPagedRep<CancelCreateOrderDTX> execute(CancelCreateOrderDTXQuery query) {
            QueryUtility.QueryContext<CancelCreateOrderDTX> queryContext = QueryUtility.prepareContext(CancelCreateOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CancelCreateOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CancelCreateOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CancelCreateOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CancelCreateOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CancelCreateOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
