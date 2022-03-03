package com.mt.shop.domain.model.notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.event.MallNotificationEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Getter
@NoArgsConstructor
public class Notification extends Auditable {
    @Id
    private Long id;
    @Embedded
    private NotificationId notificationId;
    private Long timestamp;
    @Lob
    private String details;
    private String orderId;
    private String changeId;
    private String name;

    public Notification(MallNotificationEvent event) {
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        details = CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getDetails());
        orderId = event.getDomainId().getDomainId();
        changeId = event.getChangeId();
        name = event.getName();
    }

}
