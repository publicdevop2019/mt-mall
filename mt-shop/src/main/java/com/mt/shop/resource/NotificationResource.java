package com.mt.shop.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.notification.representation.NotificationRepresentation;
import com.mt.shop.domain.model.notification.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "notifications")
public class NotificationResource {
    @GetMapping
    public ResponseEntity<SumPagedRep<NotificationRepresentation>> getNotifications(@RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                    @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<Notification> mallNotificationSumPagedRep = ApplicationServiceRegistry.getNotificationApplicationService().notificationsOf(pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(mallNotificationSumPagedRep, NotificationRepresentation::new));
    }
}
