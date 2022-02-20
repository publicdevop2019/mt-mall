package com.mt.user_profile.domain.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.user_profile.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateForAddressUpdateFailed extends DomainEvent {
    private long taskId;
    public static final String name="ORDER_UPDATE_FOR_ADDRESS_UPDATE_FAILED";
    public OrderUpdateForAddressUpdateFailed(long taskId) {
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.ORDER_UPDATE_FOR_ADDRESS_UPDATE_FAILED);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
