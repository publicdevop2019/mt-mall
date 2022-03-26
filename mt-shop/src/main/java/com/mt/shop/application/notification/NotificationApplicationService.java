package com.mt.shop.application.notification;

import com.mt.common.domain.CommonDomainRegistry;

import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.notification.representation.NotificationWebSocketRepresentation;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.notification.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationApplicationService {

    public static final String MALL_NOTIFICATION = "Notification";

    @Transactional
    
    public void handle(MallNotificationEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(event.getId().toString(), (command) -> {
            Notification notification = new Notification(event);
            DomainRegistry.getNotificationRepository().add(notification);
            DomainRegistry.getNotificationService().notify(CommonDomainRegistry.getCustomObjectSerializer().serialize(new NotificationWebSocketRepresentation(notification)));
            return null;
        }, MALL_NOTIFICATION);
    }

    public SumPagedRep<Notification> notificationsOf(String pageParam, String skipCount) {
        return DomainRegistry.getNotificationRepository().latestMallNotifications(PageConfig.limited(pageParam, 200), new QueryConfig(skipCount));
    }
}
