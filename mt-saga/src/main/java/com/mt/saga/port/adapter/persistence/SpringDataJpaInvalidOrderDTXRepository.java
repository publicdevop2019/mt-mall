package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTXQuery;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTXRepository;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaInvalidOrderDTXRepository extends JpaRepository<InvalidOrderDTX, Long>, InvalidOrderDTXRepository {

    @Override
    default void createOrUpdate(InvalidOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<InvalidOrderDTX> getById(Long id) {
        return findById(id);
    }

    @Override
    default SumPagedRep<InvalidOrderDTX> query(InvalidOrderDTXQuery query) {
        return QueryBuilderRegistry.getInvalidOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiInvalidOrderDTXAdapter {
        public SumPagedRep<InvalidOrderDTX> execute(InvalidOrderDTXQuery query) {
            QueryUtility.QueryContext<InvalidOrderDTX> queryContext = QueryUtility.prepareContext(InvalidOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), InvalidOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), InvalidOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), InvalidOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), InvalidOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(InvalidOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

}
