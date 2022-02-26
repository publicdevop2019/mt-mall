package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderForRecycleReplyEvent extends DomainEvent {
    private boolean emptyOpt;
    private long taskId;
    private String replyOf;
    public static final String name="UPDATE_ORDER_FOR_RECYCLE_REPLY_EVENT";
    public UpdateOrderForRecycleReplyEvent(boolean emptyOpt, long taskId) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf = AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT;
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
