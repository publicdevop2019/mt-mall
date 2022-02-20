package com.mt.user_profile.domain.biz_order;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class BizOrderQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String STATUS = "status";
    private Set<BizOrderId> bizOrderIds;
    private boolean admin = false;
    @Setter(AccessLevel.PRIVATE)
    private String userId;
    private BizOrderSort bizOrderSort;
    private Boolean paid;
    private Boolean orderSku;
    private Boolean actualSku;
    private Boolean requestCancel;

    public BizOrderQuery(String queryParam, String pageParam, String skipCount, boolean isAdmin) {
        this.admin = isAdmin;
        updateParam(queryParam);
        setUserId(UserThreadLocal.get());
        setPageConfig(PageConfig.limited(pageParam, 200));
        setQueryConfig(new QueryConfig(skipCount));
        setBizOrderSort(this.pageConfig);
        new BizOrderQueryValidator(this, new HttpValidationNotificationHandler()).validate();
    }

    public BizOrderQuery(BizOrderId bizOrderId, boolean isAdmin) {
        this.admin = isAdmin;
        this.bizOrderIds = Collections.singleton(bizOrderId);
        setUserId(UserThreadLocal.get());
        setQueryConfig(QueryConfig.skipCount());
        setPageConfig(PageConfig.defaultConfig());
        setBizOrderSort(this.pageConfig);
        new BizOrderQueryValidator(this, new HttpValidationNotificationHandler()).validate();
    }

    private void updateParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,ID,STATUS);
        stringStringMap.forEach((k, v) -> {
            if (k.equalsIgnoreCase(ID)) {
                bizOrderIds = Arrays.stream(v.split("\\.")).map(BizOrderId::new).collect(Collectors.toSet());
            }
            if (k.equalsIgnoreCase(STATUS)) {
                BizOrderStatus bizOrderStatus;
                try {
                    bizOrderStatus = BizOrderStatus.valueOf(v.toUpperCase());
                } catch (Exception ex) {
                    log.error("wrong enum value", ex);
                    throw new IllegalArgumentException("wrong enum value");
                }
                if (BizOrderStatus.CONFIRMED.equals(bizOrderStatus)) {
                    this.requestCancel = false;
                    this.paid = true;
                    this.orderSku = true;
                    this.actualSku = true;
                } else if (BizOrderStatus.PAID_RESERVED.equals(bizOrderStatus)) {
                    this.requestCancel = false;
                    this.paid = true;
                    this.orderSku = true;
                    this.actualSku = false;
                } else if (BizOrderStatus.PAID_RECYCLED.equals(bizOrderStatus)) {
                    this.requestCancel = false;
                    this.paid = true;
                    this.orderSku = false;
                    this.actualSku = false;
                } else if (BizOrderStatus.NOT_PAID_RECYCLED.equals(bizOrderStatus)) {
                    this.requestCancel = false;
                    this.paid = false;
                    this.orderSku = false;
                    this.actualSku = false;
                } else if (BizOrderStatus.NOT_PAID_RESERVED.equals(bizOrderStatus)) {
                    this.requestCancel = false;
                    this.paid = false;
                    this.orderSku = true;
                    this.actualSku = false;
                } else if (BizOrderStatus.CANCELLED.equals(bizOrderStatus)) {
                    this.requestCancel = true;
                } else {
                    throw new IllegalArgumentException("unknown order status");
                }
            }
        });
    }

    private void setBizOrderSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase(ID)) {
            this.bizOrderSort = BizOrderSort.byId(pageConfig.isSortOrderAsc());
        }
        Validator.notNull(this.bizOrderSort);
    }

    @Getter
    public static class BizOrderSort {
        private final boolean isById = true;
        private final boolean isAsc;

        private BizOrderSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static BizOrderSort byId(boolean isAsc) {
            return new BizOrderSort(isAsc);
        }
    }
}
