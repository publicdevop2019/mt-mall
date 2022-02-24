package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Getter
@Setter
public class CancelSaveNewOrderForCreateReplyEvent extends DomainEvent {
    public static final String name = "CANCEL_SAVE_NEW_ORDER_FOR_CREATE_REPLY_EVENT";
    private boolean emptyOpt;
    private long taskId;
    private String replyOf;
    public CancelSaveNewOrderForCreateReplyEvent(boolean emptyOpt, long taskId) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf = MQHelper.cancelOf(AppConstant.SAVE_NEW_ORDER_EVENT);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
