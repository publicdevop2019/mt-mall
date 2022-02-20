package com.mt.shop.domain.model.catalog.event;

import com.mt.shop.domain.model.catalog.CatalogId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CatalogUpdated extends CatalogEvent{
    public CatalogUpdated(CatalogId catalogId) {
        super(catalogId);
    }
}
