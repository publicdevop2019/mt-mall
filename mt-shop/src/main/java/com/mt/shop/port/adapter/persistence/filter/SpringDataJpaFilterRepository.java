package com.mt.shop.port.adapter.persistence.filter;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.shop.domain.model.filter.*;
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
public interface SpringDataJpaFilterRepository extends FilterRepository, JpaRepository<Filter, Long> {

    default Optional<Filter> filterOfId(FilterId filterId) {
        return getFilterOfId(filterId);
    }

    private Optional<Filter> getFilterOfId(FilterId filterId) {
        SumPagedRep<Filter> execute = filtersOfQuery(new FilterQuery(filterId));
        return execute.findFirst();
    }

    default void add(Filter client) {
        save(client);
    }

    default void remove(Filter filter) {
        filter.setDeleted(true);
        save(filter);
    }

    default void remove(Set<Filter> filters) {
        filters.forEach(e->{
            e.setDeleted(true);
        });
        saveAll(filters);
    }

    default SumPagedRep<Filter> filtersOfQuery(FilterQuery query) {
        if (query.getTagId() != null) {
            //in-memory search
            List<Filter> all2 = findAll();
            List<Filter> collect = all2.stream().filter(e -> e.getFilterItems().stream().anyMatch(ee -> ee.getTagId().equals(query.getTagId()))).collect(Collectors.toList());
            long offset = query.getPageConfig().getPageSize() * query.getPageConfig().getPageNumber();
            List<Filter> collect1 = IntStream.range(0, collect.size()).filter(i -> i >= offset && i < (offset + query.getPageConfig().getPageSize())).boxed().map(collect::get).collect(Collectors.toList());
            return new SumPagedRep<>(collect1, (long) collect.size());
        }
        return QueryBuilderRegistry.getFilterSelectQueryBuilder().execute(query);
    }

    @Component
    class JpaCriteriaApiFilterAdaptor {

        public SumPagedRep<Filter> execute(FilterQuery filterQuery) {
            QueryUtility.QueryContext<Filter> queryContext = QueryUtility.prepareContext(Filter.class, filterQuery);
            Optional.ofNullable(filterQuery.getCatalog()).ifPresent(e -> QueryUtility.addStringEqualPredicate(filterQuery.getCatalog(), Filter_.CATALOGS, queryContext));
            Optional.ofNullable(filterQuery.getCatalogs()).ifPresent(e -> QueryUtility.addStringLikePredicate(filterQuery.getCatalogs(), Filter_.CATALOGS, queryContext));
            Optional.ofNullable(filterQuery.getFilterIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(filterQuery.getFilterIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Filter_.FILTER_ID, queryContext));
            Order order = null;
            if (filterQuery.getFilterSort().isById())
                order = QueryUtility.getDomainIdOrder(Filter_.FILTER_ID, queryContext, filterQuery.getFilterSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(filterQuery, queryContext);
        }
    }
}
