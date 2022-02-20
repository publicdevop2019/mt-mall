package com.mt.shop.domain.model.filter;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import com.mt.shop.domain.model.tag.TagId;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class FilterQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String CATALOG = "catalog";
    public static final String CATALOGS = "catalogs";
    private Set<FilterId> filterIds;
    private String catalog;
    private String catalogs;
    private TagId tagId;
    private boolean isPublic = false;

    private FilterSort filterSort;

    public FilterQuery(String queryParam, String pageParam, String skipCount, boolean isPublic) {
        this.isPublic = isPublic;
        setPageConfig(pageParam);
        setQueryConfig(new QueryConfig(skipCount));
        setFilterSort(this.pageConfig);
        updateQueryParam(queryParam);
    }

    public FilterQuery(FilterId id) {
        this.filterIds = new HashSet<>(List.of(id));
        setPageConfig();
        setQueryConfig(QueryConfig.skipCount());
        setFilterSort(this.pageConfig);
    }

    public FilterQuery(TagId tagId) {
        setQueryConfig(QueryConfig.countRequired());
        setPageConfig();
        this.tagId = tagId;
    }

    private void setFilterSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id")) {
            this.filterSort = FilterSort.byId(pageConfig.isSortOrderAsc());
        }
        Validator.notNull(this.filterSort);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,ID,CATALOG,CATALOGS);
        this.catalog = stringStringMap.get(CATALOG);
        this.catalogs = stringStringMap.get(CATALOGS);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            this.filterIds = Arrays.stream(e.split("\\.")).map(FilterId::new).collect(Collectors.toSet());
        });
        if (isPublic) {
            if (this.catalog == null && this.catalogs == null && (this.filterIds == null || this.filterIds.isEmpty())) {
                throw new IllegalArgumentException("filter public query must have query value");
            }
        }
    }

    private void setPageConfig(String pageParam) {
        if (this.isPublic) {
            this.pageConfig = new PageConfig(pageParam, 5);
        } else {
            this.pageConfig = new PageConfig(pageParam, 400);
        }
    }

    private void setPageConfig() {
        this.pageConfig = PageConfig.defaultConfig();
    }

    public FilterSort getFilterSort() {
        return this.filterSort;
    }

    @Getter
    public static class FilterSort {
        private final boolean isById = true;
        private final boolean isAsc;

        public static FilterSort byId(boolean isAsc) {
            return new FilterSort(isAsc);
        }

        private FilterSort(boolean isAsc) {
            this.isAsc = isAsc;
        }
    }
}
