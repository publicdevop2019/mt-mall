package com.mt.shop.domain.model.filter;

import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.catalog.Catalog;
import com.mt.shop.domain.model.catalog.CatalogId;
import com.mt.shop.domain.model.catalog.CatalogQuery;
import com.mt.shop.domain.model.catalog.Type;
import com.mt.shop.domain.model.tag.Tag;
import com.mt.shop.domain.model.tag.TagId;
import com.mt.shop.domain.model.tag.TagQuery;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilterValidationService {
    public void validateCatalogs(Set<CatalogId> catalogs, HttpValidationNotificationHandler handler) {
        // filter can only be attached to frontend catalog
        Set<Catalog> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getCatalogRepository().catalogsOfQuery((CatalogQuery) query), new CatalogQuery(catalogs));
        if (allByQuery.size() != catalogs.size())
            handler.handleError("can not find all catalogs");
        Optional<Catalog> any = allByQuery.stream().filter(e -> Type.BACKEND.equals(e.getType())).findAny();
        if (any.isPresent())
            handler.handleError("filter can only be attached to frontend catalog");

    }

    public void validateTags(Set<FilterItem> filterItems, HttpValidationNotificationHandler handler) {
        Set<TagId> collect = filterItems.stream().map(FilterItem::getTagId).collect(Collectors.toSet());
        Set<Tag> tagSet = QueryUtility.getAllByQuery((query) -> DomainRegistry.getTagRepository().tagsOfQuery((TagQuery) query), new TagQuery(collect));
        if (collect.size() != tagSet.size())
            handler.handleError("can not find all tags");
    }
}
