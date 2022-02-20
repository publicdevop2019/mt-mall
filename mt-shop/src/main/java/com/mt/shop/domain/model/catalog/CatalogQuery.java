package com.mt.shop.domain.model.catalog;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.shop.domain.model.tag.TagId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;


@Getter
public class CatalogQuery extends QueryCriteria {
    private static final String TYPE_LITERAL = "type";
    private static final String PARENT_ID_LITERAL = "parentId";
    public static final String ID = "id";
    public static final String NAME = "name";
    @Setter(AccessLevel.PRIVATE)
    private Set<CatalogId> catalogIds;
    @Setter(AccessLevel.PRIVATE)
    private CatalogId parentId;
    @Setter(AccessLevel.PRIVATE)
    private Type type;
    private CatalogSort catalogSort;
    private TagId tagId;
    private String name;

    public CatalogQuery(String query, String pageConfig, String queryConfig) {
        setPageConfig(PageConfig.limited(pageConfig, 2000));
        setQueryConfig(new QueryConfig(queryConfig));
        updateQueryParam(query);
    }

    public CatalogQuery(TagId tagId) {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        this.tagId=tagId;
    }

    protected void setPageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
        setCatalogSort();
    }

    private void updateQueryParam(String query) {
        Map<String, String> queryMap = QueryUtility.parseQuery(query, ID, TYPE_LITERAL, PARENT_ID_LITERAL,NAME);
        if (queryMap.get(ID) != null) {
            String id = queryMap.get(ID);
            setCatalogIds(Arrays.stream(id.split("\\.")).map(CatalogId::new).collect(Collectors.toSet()));
        }
        if (queryMap.get(TYPE_LITERAL) != null) {
            String type = queryMap.get(TYPE_LITERAL);
            if (type.equalsIgnoreCase("frontend")) {
                setType(Type.FRONTEND);
            } else if (type.equalsIgnoreCase("backend")) {
                setType(Type.BACKEND);
            } else {
                throw new IllegalArgumentException("unable to parse string type enum");
            }
        }
        if (queryMap.get(PARENT_ID_LITERAL) != null) {
            String parentId = queryMap.get(PARENT_ID_LITERAL);
            setParentId(new CatalogId(parentId));
        }
        if (queryMap.get(NAME) != null) {
            name = queryMap.get(NAME);
        }
    }

    private void setCatalogSort() {
        if (pageConfig.getSortBy().equalsIgnoreCase("name"))
            this.catalogSort = CatalogSort.byName(pageConfig.isSortOrderAsc());
        if (pageConfig.getSortBy().equalsIgnoreCase(ID))
            this.catalogSort = CatalogSort.byCatalogId(pageConfig.isSortOrderAsc());
    }

    private CatalogQuery() {
    }

    public CatalogQuery(CatalogId catalogId) {
        this.catalogIds = new HashSet<>(List.of(catalogId));
        setQueryConfig(QueryConfig.skipCount());
        setPageConfig(PageConfig.defaultConfig());
    }

    public CatalogQuery(Set<CatalogId> catalogIds) {
        this.catalogIds = catalogIds;
        setQueryConfig(QueryConfig.countRequired());
        setPageConfig(PageConfig.defaultConfig());
    }

    public static CatalogQuery publicQuery(String pageConfig, String queryConfig) {
        CatalogQuery catalogQuery = new CatalogQuery();
        catalogQuery.setPageConfig(new PageConfig(pageConfig, 1500));
        catalogQuery.setType(Type.FRONTEND);
        catalogQuery.setQueryConfig(new QueryConfig(queryConfig));
        return catalogQuery;
    }

    @Getter
    public static class CatalogSort {
        private boolean byName;
        private boolean byId;
        private final boolean isAscending;

        private void setByName() {
            this.byName = true;
        }

        private void setById() {
            this.byId = true;
        }

        private CatalogSort(boolean isAscending) {
            this.isAscending = isAscending;
        }

        public static CatalogSort byName(boolean isAscending) {
            CatalogSort catalogSort = new CatalogSort(isAscending);
            catalogSort.setByName();
            return catalogSort;
        }

        public static CatalogSort byCatalogId(boolean isAscending) {
            CatalogSort catalogSort = new CatalogSort(isAscending);
            catalogSort.setById();
            return catalogSort;
        }
    }
}
