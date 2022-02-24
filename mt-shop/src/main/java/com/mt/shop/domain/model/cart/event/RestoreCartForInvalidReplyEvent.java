package com.mt.shop.domain.model.cart.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Getter
@Setter
public class RestoreCartForInvalidReplyEvent extends DomainEvent {
    public static final String name = "RESTORE_CART_FOR_INVALID_REPLY_EVENT";
    private long taskId;
    private boolean emptyOpt;
    private String replyOf;
    public RestoreCartForInvalidReplyEvent(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf=AppConstant.RESTORE_CART_FOR_INVALID_EVENT;
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
