package com.mt.shop.domain.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForRecycleReplyCommand extends DomainEvent {
    public static final String name = "CANCEL_UPDATE_ORDER_FOR_RECYCLE_REPLY_COMMAND";
    private long taskId;
    private boolean emptyOpt;

    public CancelUpdateOrderForRecycleReplyCommand(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(MQHelper.replyCancelOf(AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
