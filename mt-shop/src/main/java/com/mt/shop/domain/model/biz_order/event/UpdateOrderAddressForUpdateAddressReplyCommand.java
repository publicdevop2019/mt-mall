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
public class UpdateOrderAddressForUpdateAddressReplyCommand extends DomainEvent {
    public static final String name = "UPDATE_ORDER_ADDRESS_FOR_UPDATE_ADDRESS_REPLY_COMMAND";
    private long taskId;
    private boolean emptyOpt;
    private String replyOf;
    public UpdateOrderAddressForUpdateAddressReplyCommand(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf = AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT;
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
