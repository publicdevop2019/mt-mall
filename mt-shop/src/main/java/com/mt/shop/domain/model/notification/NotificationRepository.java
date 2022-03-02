package com.mt.shop.domain.model.notification;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;

public interface NotificationRepository {
    void add(Notification notification);

    SumPagedRep<Notification> latestMallNotifications(PageConfig defaultPaging, QueryConfig queryConfig);
}
