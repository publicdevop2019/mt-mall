package com.mt.saga.domain.model.distributed_tx;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class DistributedTxQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String ORDER_ID = "orderId";
    public static final String CHANGE_ID = "changeId";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    private Set<Long> ids;
    private Set<String> orderIds;
    private Set<String> changeIds;
    private DTXStatus status;
    private Sort sort;
    private Set<String> names;

    public DistributedTxQuery(String queryParam, String pageParam, String skipCount) {
        setQueryConfig(new QueryConfig(skipCount));
        setPageConfig(PageConfig.limited(pageParam, 200));
        updateQueryParam(queryParam);
        setSort(pageConfig);

    }

    public DistributedTxQuery(String changeId) {
        setQueryConfig(QueryConfig.skipCount());
        setPageConfig(PageConfig.defaultConfig());
        this.changeIds = Collections.singleton(changeId);
        setSort(pageConfig);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, ORDER_ID, CHANGE_ID, STATUS,NAME);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            this.ids = Arrays.stream(e.split("\\.")).map(Long::parseLong).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> {
            this.names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet());
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
            this.sort = Sort.byId(pageConfig.isSortOrderAsc());
        }
    }

    @Getter
    public static class Sort {
        private final boolean isAsc;
        private boolean isById;

        private Sort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static Sort byId(boolean isAsc) {
            Sort skuSort = new Sort(isAsc);
            skuSort.isById = true;
            return skuSort;
        }
    }
}
