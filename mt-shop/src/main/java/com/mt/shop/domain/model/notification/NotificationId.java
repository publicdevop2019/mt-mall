package com.mt.shop.domain.model.notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

public class NotificationId extends DomainId {
    public NotificationId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("4M" + s.toUpperCase());
    }

    public NotificationId(String domainId) {
        super(domainId);
    }
}
