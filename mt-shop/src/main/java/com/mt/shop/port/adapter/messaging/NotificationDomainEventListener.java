package com.mt.shop.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.shop.application.ApplicationServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDomainEventListener {
    @EventListener(ApplicationReadyEvent.class)
    protected void listener() {
        CommonDomainRegistry.getEventStreamService().of("*", false, AppInfo.EventName.MT3_MALL_NOTIFICATION, (event) -> {
            MallNotificationEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), MallNotificationEvent.class);
            ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
        });
    }
}
