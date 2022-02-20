package com.mt.user_profile.domain.cart.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.user_profile.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestoreCartForInvalidFailedEvent extends DomainEvent {
    private long taskId;
    public static final String name="RESTORE_CART_FAILED_EVENT";
    public RestoreCartForInvalidFailedEvent(long taskId) {
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.RESTORE_CART_FAILED_EVENT);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
