package com.mt.saga.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX_;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTXQuery;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTXRepository;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.criteria.Order;
import java.util.Optional;

@Repository
public interface SpringDataJpaCancelConfirmOrderPaymentDTXRepository extends JpaRepository<CancelConfirmOrderPaymentDTX, Long>, CancelConfirmOrderPaymentDTXRepository {


    @Override
    default void createOrUpdate(CancelConfirmOrderPaymentDTX createOrderTask) {
        save(createOrderTask);
    }

    @Override
    default Optional<CancelConfirmOrderPaymentDTX> getById(Long id) {
        return findById(id);
    }
    @Override
    default SumPagedRep<CancelConfirmOrderPaymentDTX> query(CancelConfirmOrderPaymentDTXQuery query) {
        return QueryBuilderRegistry.getCancelConfirmOrderPaymentDTXAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiCancelConfirmOrderPaymentDTXAdapter {
        public SumPagedRep<CancelConfirmOrderPaymentDTX> execute(CancelConfirmOrderPaymentDTXQuery query) {
            QueryUtility.QueryContext<CancelConfirmOrderPaymentDTX> queryContext = QueryUtility.prepareContext(CancelConfirmOrderPaymentDTX.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), CancelConfirmOrderPaymentDTX_.ID, queryContext));
            Optional.ofNullable(query.getOrderIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getOrderIds(), CancelConfirmOrderPaymentDTX_.ORDER_ID, queryContext));
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getChangeIds(), CancelConfirmOrderPaymentDTX_.CHANGE_ID, queryContext));
            Optional.ofNullable(query.getStatus()).ifPresent(e -> QueryUtility.addStringEqualPredicate(query.getStatus().toString(), CancelConfirmOrderPaymentDTX_.STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(CancelConfirmOrderPaymentDTX_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
