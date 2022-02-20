package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX_;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTXQuery;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTXRepository;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaConfirmOrderPaymentDTXRepository extends JpaRepository<ConfirmOrderPaymentDTX, Long>, ConfirmOrderPaymentDTXRepository {

    @Override
    default void createOrUpdate(ConfirmOrderPaymentDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<ConfirmOrderPaymentDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<ConfirmOrderPaymentDTX> query(ConfirmOrderPaymentDTXQuery query) {
        return QueryBuilderRegistry.getConfirmOrderPaymentDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiConfirmOrderPaymentDTXAdapter {
        public SumPagedRep<ConfirmOrderPaymentDTX> execute(ConfirmOrderPaymentDTXQuery query) {
            QueryUtility.QueryContext<ConfirmOrderPaymentDTX> queryContext = QueryUtility.prepareContext(ConfirmOrderPaymentDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), ConfirmOrderPaymentDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), ConfirmOrderPaymentDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), ConfirmOrderPaymentDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), ConfirmOrderPaymentDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(ConfirmOrderPaymentDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

}
