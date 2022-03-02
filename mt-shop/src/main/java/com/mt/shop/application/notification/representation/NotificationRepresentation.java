package com.mt.shop.application.notification.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.shop.domain.model.notification.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@Getter
public class NotificationRepresentation implements Serializable {
    private Long date;
    private Map<String, String> detail;
    private String orderId;
    private String changeId;
    private String name;

    public NotificationRepresentation(Object o) {
        Notification notification = (Notification) o;
        date = notification.getTimestamp();
        orderId = notification.getOrderId();
        name = notification.getName();
        changeId = notification.getChangeId();
        detail = CommonDomainRegistry.getCustomObjectSerializer().deserializeToMap(notification.getDetails(), String.class, String.class);

    }
}
