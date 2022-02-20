package com.mt.shop.domain.model.filter;

import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.catalog.CatalogId;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FilterService {
    public FilterId create(FilterId filterId, String des, Set<CatalogId> catalogs, Set<FilterItem> filter1) {
        Filter filter = new Filter(filterId, catalogs, filter1, des);
        DomainRegistry.getFilterRepository().add(filter);
        return filter.getFilterId();
    }
}
