package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXRepository;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCancelConcludeOrderDTXRepository extends JpaRepository<CancelConcludeOrderDTX, Long>, CancelConcludeOrderDTXRepository {

    @Override
    default void createOrUpdate(CancelConcludeOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CancelConcludeOrderDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<CancelConcludeOrderDTX> query(CancelConcludeOrderDTXQuery query) {
        return QueryBuilderRegistry.getCancelConcludeOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCancelConcludeOrderDTXAdapter {
        public SumPagedRep<CancelConcludeOrderDTX> execute(CancelConcludeOrderDTXQuery query) {
            QueryUtility.QueryContext<CancelConcludeOrderDTX> queryContext = QueryUtility.prepareContext(CancelConcludeOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CancelConcludeOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CancelConcludeOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CancelConcludeOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CancelConcludeOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CancelConcludeOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
