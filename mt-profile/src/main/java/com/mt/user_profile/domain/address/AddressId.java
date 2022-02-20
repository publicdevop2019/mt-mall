package com.mt.user_profile.domain.address;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import javax.persistence.Embeddable;

@Embeddable
public class AddressId extends DomainId {
    public AddressId() {
        super();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("2A" + s.toUpperCase());
    }

    public AddressId(String domainId) {
        super(domainId);
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
