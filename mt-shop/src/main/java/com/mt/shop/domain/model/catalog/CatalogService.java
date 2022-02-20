package com.mt.shop.domain.model.catalog;

import com.mt.shop.domain.DomainRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CatalogService {
    public CatalogId create(CatalogId catalogId, String name, CatalogId parentId, Set<LinkedTag> attributes, Type catalogType) {
        Catalog catalog = new Catalog(catalogId, name, parentId, attributes, catalogType);
        DomainRegistry.getCatalogRepository().add(catalog);
        return catalog.getCatalogId();
    }
}
