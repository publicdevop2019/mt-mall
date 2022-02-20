package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTXQuery;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTXRepository;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCancelInvalidOrderDTXRepository extends JpaRepository<CancelInvalidOrderDTX, Long>, CancelInvalidOrderDTXRepository {

    @Override
    default void createOrUpdate(CancelInvalidOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CancelInvalidOrderDTX> getById(Long id) {
        return findById(id);
    }

    @Override
    default SumPagedRep<CancelInvalidOrderDTX> query(CancelInvalidOrderDTXQuery query) {
        return QueryBuilderRegistry.getCancelInvalidOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCancelInvalidOrderDTXAdapter {
        public SumPagedRep<CancelInvalidOrderDTX> execute(CancelInvalidOrderDTXQuery query) {
            QueryUtility.QueryContext<CancelInvalidOrderDTX> queryContext = QueryUtility.prepareContext(CancelInvalidOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CancelInvalidOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CancelInvalidOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CancelInvalidOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CancelInvalidOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CancelInvalidOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

}
