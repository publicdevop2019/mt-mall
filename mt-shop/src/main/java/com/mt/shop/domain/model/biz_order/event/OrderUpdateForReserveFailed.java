package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateForReserveFailed extends DomainEvent {
    public static final String name = "ORDER_UPDATE_FOR_RESERVE_FAILED";
    private long taskId;

    public OrderUpdateForReserveFailed(long taskId) {
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.ORDER_UPDATE_FOR_RESERVE_FAILED);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}

