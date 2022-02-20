package com.mt.saga.domain.model.recycle_order_dtx;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.saga.domain.model.common.DTXStatus;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class RecycleOrderDTXQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String ORDER_ID = "orderId";
    public static final String CHANGE_ID = "changeId";
    public static final String STATUS = "status";
    private Set<Long> ids;
    private Set<String> orderIds;
    private Set<String> changeIds;
    private DTXStatus status;
    private RecycleOrderDTXSort sort;
    public RecycleOrderDTXQuery(String queryParam, String pageParam, String skipCount) {
        setQueryConfig(new QueryConfig(skipCount));
        setPageConfig(PageConfig.limited(pageParam, 200));
        updateQueryParam(queryParam);
       setSort(pageConfig);

    }

    public RecycleOrderDTXQuery(String changeId) {
        setQueryConfig(QueryConfig.skipCount());
        setPageConfig(PageConfig.defaultConfig());
        this.changeIds= Collections.singleton(changeId);
        setSort(pageConfig);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,ID,ORDER_ID,CHANGE_ID,STATUS);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            this.ids = Arrays.stream(e.split("\\.")).map(Long::parseLong).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(ORDER_ID)).ifPresent(e -> {
            this.orderIds = Arrays.stream(e.split("\\.")).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(CHANGE_ID)).ifPresent(e -> {
            this.changeIds = Arrays.stream(e.split("\\.")).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(STATUS)).ifPresent(e -> {
            this.status = DTXStatus.valueOf(e);
        });
    }

    private void setSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id")) {
            this.sort = RecycleOrderDTXSort.byId(pageConfig.isSortOrderAsc());
        }
    }
    @Getter
    public static class RecycleOrderDTXSort {
        private boolean isById;
        private final boolean isAsc;

        private RecycleOrderDTXSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static RecycleOrderDTXSort byId(boolean isAsc) {
            RecycleOrderDTXSort skuSort = new RecycleOrderDTXSort(isAsc);
            skuSort.isById = true;
            return skuSort;
        }
    }
}
