package com.mt.shop.domain.cart.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClearCartForCreateFailedEvent extends DomainEvent {
    public static final String name = "CLEAR_CART_FAILED_EVENT";
    private long taskId;

    public ClearCartForCreateFailedEvent(long taskId) {
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.CLEAR_CART_FAILED_EVENT);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
