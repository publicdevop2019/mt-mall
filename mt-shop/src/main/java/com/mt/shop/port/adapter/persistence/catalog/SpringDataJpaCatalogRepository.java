package com.mt.shop.port.adapter.persistence.catalog;

import com.mt.common.CommonConstant;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.shop.domain.model.catalog.*;
import com.mt.shop.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public interface SpringDataJpaCatalogRepository extends CatalogRepository, JpaRepository<Catalog, Long> {

    default Optional<Catalog> catalogOfId(CatalogId catalogId) {
        SumPagedRep<Catalog> execute = catalogsOfQuery(new CatalogQuery(catalogId));
        return execute.findFirst();
    }

    default SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery query) {
        if (query.getTagId() != null) {
            //in-memory search
            List<Catalog> all2 = findAll();
            List<Catalog> collect = all2.stream().filter(e -> e.getLinkedTags().stream().anyMatch(ee -> ee.getTagId().equals(query.getTagId()))).collect(Collectors.toList());
            long offset = query.getPageConfig().getPageSize() * query.getPageConfig().getPageNumber();
            List<Catalog> collect1 = IntStream.range(0, collect.size()).filter(i -> i >= offset && i < (offset + query.getPageConfig().getPageSize())).boxed().map(collect::get).collect(Collectors.toList());
            return new SumPagedRep<>(collect1, (long) collect.size());
        }
        return QueryBuilderRegistry.getCatalogSelectQueryBuilder().execute(query);
    }

    default void add(Catalog client) {
        save(client);
    }

    default void remove(Catalog client) {
        client.setDeleted(true);
        save(client);
    }

    default void remove(Set<Catalog> client) {
        client.forEach(e->{
            e.setDeleted(true);
        });
        saveAll(client);
    }

    @Component
    class JpaCriteriaApiCatalogAdaptor {

        public SumPagedRep<Catalog> execute(CatalogQuery catalogQuery) {
            QueryUtility.QueryContext<Catalog> queryContext = QueryUtility.prepareContext(Catalog.class, catalogQuery);
            Optional.ofNullable(catalogQuery.getType()).ifPresent(e -> QueryUtility.addStringEqualPredicate(catalogQuery.getType().name(), Catalog_.TYPE, queryContext));
            Optional.ofNullable(catalogQuery.getName()).ifPresent(e -> QueryUtility.addStringLikePredicate(catalogQuery.getName(), Catalog_.NAME, queryContext));
            Optional.ofNullable(catalogQuery.getParentId()).ifPresent(e -> addParentIdPredicate(catalogQuery.getParentId().getDomainId(), Catalog_.PARENT_ID, queryContext));
            Optional.ofNullable(catalogQuery.getCatalogIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(catalogQuery.getCatalogIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Catalog_.CATALOG_ID, queryContext));
            Order order = null;
            if (catalogQuery.getCatalogSort().isById())
                order = QueryUtility.getDomainIdOrder(Catalog_.CATALOG_ID, queryContext, catalogQuery.getCatalogSort().isAscending());
            if (catalogQuery.getCatalogSort().isByName())
                order = QueryUtility.getOrder(Catalog_.NAME, queryContext, catalogQuery.getCatalogSort().isAscending());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(catalogQuery, queryContext);
        }
    }
    private static <T> void addParentIdPredicate(String value, String sqlFieldName, QueryUtility.QueryContext<T> context) {
        if("null".equalsIgnoreCase(value)){
            context.getPredicates().add(context.getCriteriaBuilder().isNull(context.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class)));
            Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(context.getCriteriaBuilder().isNull(context.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class))));

        }else{
            context.getPredicates().add(context.getCriteriaBuilder().equal(context.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class), value));
            Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(context.getCriteriaBuilder().equal(context.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class), value)));

        }
    }
}
