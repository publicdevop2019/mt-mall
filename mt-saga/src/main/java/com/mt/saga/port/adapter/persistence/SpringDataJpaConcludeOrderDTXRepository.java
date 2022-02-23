package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTXQuery;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTXRepository;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaConcludeOrderDTXRepository extends JpaRepository<ConcludeOrderDTX, Long>, ConcludeOrderDTXRepository {

    @Override
    default Optional<ConcludeOrderDTX> getById(Long id) {
        return findById(id);
    }

    @Override
    default void createOrUpdate(ConcludeOrderDTX createOrderTask) {
        save(createOrderTask);
    }
    @Override
    default SumPagedRep<ConcludeOrderDTX> query(ConcludeOrderDTXQuery query) {
        return QueryBuilderRegistry.getConcludeOrderDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiConcludeOrderDTXAdapter {
        public SumPagedRep<ConcludeOrderDTX> execute(ConcludeOrderDTXQuery query) {
            QueryUtility.QueryContext<ConcludeOrderDTX> queryContext = QueryUtility.prepareContext(ConcludeOrderDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), ConcludeOrderDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), ConcludeOrderDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), ConcludeOrderDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), ConcludeOrderDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(ConcludeOrderDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

}
