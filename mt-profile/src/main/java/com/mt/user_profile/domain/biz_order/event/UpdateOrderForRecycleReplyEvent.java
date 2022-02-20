package com.mt.user_profile.domain.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.user_profile.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderForRecycleReplyEvent extends DomainEvent {
    private boolean emptyOpt;
    private long taskId;
    public static final String name="UPDATE_ORDER_FOR_RECYCLE_REPLY_EVENT";
    public UpdateOrderForRecycleReplyEvent(boolean emptyOpt, long taskId) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic(MQHelper.replyOf(AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
