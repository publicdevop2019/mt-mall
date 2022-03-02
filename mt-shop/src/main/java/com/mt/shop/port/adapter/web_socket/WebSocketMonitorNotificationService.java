package com.mt.shop.port.adapter.web_socket;

import com.mt.shop.domain.model.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMonitorNotificationService implements NotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.MallMonitorHandler mallMonitorHandler;

    @Override
    public void notify(String message) {
        mallMonitorHandler.broadcast(message);
    }
}
