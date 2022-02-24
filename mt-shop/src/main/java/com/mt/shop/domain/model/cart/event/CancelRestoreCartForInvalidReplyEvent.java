package com.mt.shop.domain.model.cart.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelRestoreCartForInvalidReplyEvent extends DomainEvent {
    public static final String name = "CANCEL_RESTORE_CART_FOR_INVALID_REPLY_EVENT";
    private boolean emptyOpt;
    private long taskId;

    public CancelRestoreCartForInvalidReplyEvent(long taskId, boolean emptyOpt) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic(MQHelper.replyCancelOf(AppConstant.RESTORE_CART_FOR_INVALID_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
