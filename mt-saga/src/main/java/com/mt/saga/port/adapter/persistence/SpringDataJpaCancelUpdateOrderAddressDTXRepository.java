package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTXQuery;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTXRepository;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCancelUpdateOrderAddressDTXRepository extends JpaRepository<CancelUpdateOrderAddressDTX, Long>, CancelUpdateOrderAddressDTXRepository {


    @Override
    default void createOrUpdate(CancelUpdateOrderAddressDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CancelUpdateOrderAddressDTX> getById(Long id) {
        return findById(id);
    }

    @Override
    default SumPagedRep<CancelUpdateOrderAddressDTX> query(CancelUpdateOrderAddressDTXQuery query) {
        return QueryBuilderRegistry.getCancelUpdateOrderAddressDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCancelUpdateOrderAddressDTXAdapter {
        public SumPagedRep<CancelUpdateOrderAddressDTX> execute(CancelUpdateOrderAddressDTXQuery query) {
            QueryUtility.QueryContext<CancelUpdateOrderAddressDTX> queryContext = QueryUtility.prepareContext(CancelUpdateOrderAddressDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CancelUpdateOrderAddressDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CancelUpdateOrderAddressDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CancelUpdateOrderAddressDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CancelUpdateOrderAddressDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CancelUpdateOrderAddressDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
