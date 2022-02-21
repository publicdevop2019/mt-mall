package com.mt.shop.domain.cart.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClearCartForCreateReplyEvent extends DomainEvent {
    public static final String name = "CLEAR_CART_REPLY_EVENT";
    private long taskId;
    private boolean emptyOpt;

    public ClearCartForCreateReplyEvent(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(MQHelper.replyOf(AppConstant.CLEAR_CART_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
