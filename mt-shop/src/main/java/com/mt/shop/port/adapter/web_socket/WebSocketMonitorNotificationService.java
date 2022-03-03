package com.mt.shop.port.adapter.web_socket;

import com.mt.shop.domain.model.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class WebSocketMonitorNotificationService implements NotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.MallMonitorHandler mallMonitorHandler;

    @Override
    public void notify(String message) {
        mallMonitorHandler.broadcast(message);
    }
    @Override
    public void renew() {
        mallMonitorHandler.broadcast("_renew");
    }
    @Scheduled(fixedRate = 25 * 1000)
    protected void autoRenew(){
        log.debug("start of renewing ws connects");
        renew();
    }
}
