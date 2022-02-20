package com.mt.user_profile.domain.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.user_profile.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderAddressForUpdateAddressReplyCommand extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
    public static final String name="UPDATE_ORDER_ADDRESS_FOR_UPDATE_ADDRESS_REPLY_COMMAND";
    public UpdateOrderAddressForUpdateAddressReplyCommand(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(MQHelper.replyOf(AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
