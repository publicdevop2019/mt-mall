package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderForInvalidReplyCommand extends DomainEvent {
    public static final String name = "REMOVE_ORDER_FOR_INVALID_REPLY_COMMAND";
    private long taskId;
    private boolean emptyOpt;
    private String replyOf;
    public UpdateOrderForInvalidReplyCommand(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf = AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT;
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
