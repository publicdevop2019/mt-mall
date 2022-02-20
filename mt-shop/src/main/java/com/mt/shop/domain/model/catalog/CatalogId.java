package com.mt.shop.domain.model.catalog;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import java.io.Serializable;

public class CatalogId extends DomainId implements Serializable {
    private static final long serialVersionUID = 1L;
    public CatalogId() {
        super();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("3C" + s.toUpperCase());
    }

    public CatalogId(String domainId) {
        super(domainId);
    }
}
