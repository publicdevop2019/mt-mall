package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelSaveNewOrderForCreateReplyEvent extends DomainEvent {
    public static final String name = "CANCEL_SAVE_NEW_ORDER_FOR_CREATE_REPLY_EVENT";
    private boolean emptyOpt;
    private long taskId;

    public CancelSaveNewOrderForCreateReplyEvent(boolean emptyOpt, long taskId) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic(MQHelper.replyCancelOf(AppConstant.SAVE_NEW_ORDER_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
