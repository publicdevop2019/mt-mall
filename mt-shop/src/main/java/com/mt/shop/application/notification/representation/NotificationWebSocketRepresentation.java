package com.mt.shop.application.notification.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.shop.domain.model.notification.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@NoArgsConstructor
@Getter
public class NotificationWebSocketRepresentation {
    private Long date;
    private String title;
    private Set<String> descriptions;

    public NotificationWebSocketRepresentation(Notification notification) {
        date = notification.getTimestamp();
        descriptions = Collections.singleton(notification.getOrderId());
        title = notification.getName();
    }

    public String value() {
        return CommonDomainRegistry.getCustomObjectSerializer().serialize(this);
    }
}
