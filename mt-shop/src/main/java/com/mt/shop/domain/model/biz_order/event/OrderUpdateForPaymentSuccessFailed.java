package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateForPaymentSuccessFailed  extends DomainEvent {
    private long taskId;
    public static final String name="ORDER_UPDATE_FOR_PAYMENT_SUCCESS_FAILED";
    public OrderUpdateForPaymentSuccessFailed(long taskId) {
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.LTX_FAILED_EVENT);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}

