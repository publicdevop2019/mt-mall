package com.mt.user_profile.domain.cart.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.user_profile.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestoreCartForInvalidReplyEvent extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
    public static final String name="RESTORE_CART_FOR_INVALID_REPLY_EVENT";
    public RestoreCartForInvalidReplyEvent(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(MQHelper.replyOf(AppConstant.RESTORE_CART_FOR_INVALID_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
