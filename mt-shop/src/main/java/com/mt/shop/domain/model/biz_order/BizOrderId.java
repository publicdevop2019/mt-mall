package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class BizOrderId extends DomainId implements Serializable {
    private static final long serialVersionUID = 1L;
    public BizOrderId() {
        super();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("2O" + s.toUpperCase());
    }

    public BizOrderId(String domainId) {
        super(domainId);
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
