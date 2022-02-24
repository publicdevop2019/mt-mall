package com.mt.shop.port.adapter.persistence.biz_order;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaBizOrderSummaryRepository extends JpaRepository<BizOrderSummary, Long>, BizOderSummaryRepository {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.modifiedByUserAt < ?1 AND p.paid = false AND p.orderSku = true AND p.deleted = false")
    List<BizOrderSummary> findExpiredNotPaidReserved(Date expireAt);

    @Query("SELECT p FROM #{#entityName} as p WHERE p.paid = true AND p.orderSku = true AND p.actualSku = false AND p.deleted = false")
    List<BizOrderSummary> findPaidReservedDraft();

    @Query("SELECT p FROM #{#entityName} as p WHERE p.paid = true AND p.orderSku = false AND p.deleted = false")
    List<BizOrderSummary> findPaidRecycled();

    default SumPagedRep<BizOrderSummary> ordersOfQuery(BizOrderQuery bizOrderQuery) {
        QueryUtility.QueryContext<BizOrderSummary> queryContext = getBizOrderSummaryQueryContext(bizOrderQuery);
        return QueryUtility.pagedQuery(bizOrderQuery, queryContext);
    }

    private QueryUtility.QueryContext<BizOrderSummary> getBizOrderSummaryQueryContext(BizOrderQuery bizOrderQuery) {
        QueryUtility.QueryContext<BizOrderSummary> queryContext = QueryUtility.prepareContext(BizOrderSummary.class, bizOrderQuery);
        Optional.ofNullable(bizOrderQuery.getBizOrderIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), BizOrderSummary_.ORDER_ID, queryContext));
        Optional.ofNullable(UserThreadLocal.get()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e, BizOrderSummary_.USER_ID, queryContext));
        Optional.ofNullable(bizOrderQuery.getPaid()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, BizOrderSummary_.PAID, queryContext));
        Optional.ofNullable(bizOrderQuery.getActualSku()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, BizOrderSummary_.ACTUAL_SKU, queryContext));
        Optional.ofNullable(bizOrderQuery.getOrderSku()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, BizOrderSummary_.ORDER_SKU, queryContext));
        Optional.ofNullable(bizOrderQuery.getRequestCancel()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, BizOrderSummary_.REQUEST_CANCEL, queryContext));
        Order order = null;
        if (bizOrderQuery.getBizOrderSort().isById())
            order = QueryUtility.getDomainIdOrder(BizOrderSummary_.ORDER_ID, queryContext, bizOrderQuery.getBizOrderSort().isAsc());
        queryContext.setOrder(order);
        return queryContext;
    }

    default SumPagedRep<BizOrderSummary> nativeOrdersOfQuery(BizOrderQuery bizOrderQuery) {
        QueryUtility.QueryContext<BizOrderSummary> queryContext = getBizOrderSummaryQueryContext(bizOrderQuery);
        return QueryUtility.nativePagedQuery(bizOrderQuery, queryContext);
    }

    default void add(BizOrderSummary bizOrder) {
        save(bizOrder);
    }

}
