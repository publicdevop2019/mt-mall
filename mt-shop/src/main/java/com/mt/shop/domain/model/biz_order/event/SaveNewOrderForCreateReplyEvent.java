package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.domain.model.biz_order.BizOrderId;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Data;

import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Data
public class SaveNewOrderForCreateReplyEvent extends DomainEvent {
    public static final String name = "SAVE_NEW_ORDER_FOR_CREATE_REPLY_EVENT";
    private final boolean emptyOpt;
    private long taskId;
    private String replyOf;

    public SaveNewOrderForCreateReplyEvent(BizOrderId orderId, boolean emptyOpt, long taskId) {
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf = AppConstant.SAVE_NEW_ORDER_EVENT;
        setDomainId(orderId);
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
