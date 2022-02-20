package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTXQuery;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTXRepository;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCreateOrderDTXRepository extends JpaRepository<CreateOrderDTX, Long>, CreateOrderDTXRepository {

    @Override
    default void createOrUpdate(CreateOrderDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CreateOrderDTX> getById(Long id) {
        return findById(id);
    }

    @Override
    default SumPagedRep<CreateOrderDTX> query(CreateOrderDTXQuery query) {
        return QueryBuilderRegistry.getCreateOrderDTXQueryAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCreateOrderDTXAdapter {
        public SumPagedRep<CreateOrderDTX> execute(CreateOrderDTXQuery query) {
            QueryUtility.QueryContext<CreateOrderDTX> queryContext = QueryUtility.prepareContext(CreateOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CreateOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CreateOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CreateOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CreateOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CreateOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
