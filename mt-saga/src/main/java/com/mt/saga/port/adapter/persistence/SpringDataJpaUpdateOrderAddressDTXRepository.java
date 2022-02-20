package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTXQuery;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTXRepository;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaUpdateOrderAddressDTXRepository extends JpaRepository<UpdateOrderAddressDTX, Long>, UpdateOrderAddressDTXRepository {

    @Override
    default void createOrUpdate(UpdateOrderAddressDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<UpdateOrderAddressDTX> getById(Long id) {
        return findById(id);
    }

    @Override
    default SumPagedRep<UpdateOrderAddressDTX> query(UpdateOrderAddressDTXQuery query) {
        return QueryBuilderRegistry.getUpdateOrderAddressDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiUpdateOrderAddressDTXAdapter {
        public SumPagedRep<UpdateOrderAddressDTX> execute(UpdateOrderAddressDTXQuery query) {
            QueryUtility.QueryContext<UpdateOrderAddressDTX> queryContext = QueryUtility.prepareContext(UpdateOrderAddressDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), UpdateOrderAddressDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), UpdateOrderAddressDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), UpdateOrderAddressDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), UpdateOrderAddressDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(UpdateOrderAddressDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

}
