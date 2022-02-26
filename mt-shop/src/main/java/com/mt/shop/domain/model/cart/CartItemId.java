package com.mt.shop.domain.model.cart;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import javax.persistence.Embeddable;

@Embeddable
public class CartItemId extends DomainId {
    public CartItemId() {
        super();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("2C" + s.toUpperCase());
    }

    public CartItemId(String domainId) {
        super(domainId);
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
