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
public class CancelUpdateOrderForInvalidReplyCommand extends DomainEvent {
    public static final String name = "CANCEL_UPDATE_ORDER_FOR_INVALID_REPLY_COMMAND";
    private long taskId;
    private boolean emptyOpt;
    private String replyOf;
    public CancelUpdateOrderForInvalidReplyCommand(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf = MQHelper.cancelOf(AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
