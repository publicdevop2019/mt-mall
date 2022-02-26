package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Date;
import java.util.List;

public interface BizOderSummaryRepository {
    SumPagedRep<BizOrderSummary> ordersOfQuery(BizOrderQuery bizOrderQuery);
    SumPagedRep<BizOrderSummary> nativeOrdersOfQuery(BizOrderQuery bizOrderQuery);

    void add(BizOrderSummary bizOrder);

    List<BizOrderSummary> findExpiredNotPaidReserved(Date from);

    List<BizOrderSummary> findPaidReservedDraft();

    List<BizOrderSummary> findPaidRecycled();
}
