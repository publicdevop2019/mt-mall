package com.mt.saga.domain.model.cancel_invalid_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.invalid_order.event.RemoveOrderForInvalidEvent;
import com.mt.saga.infrastructure.AppConstant;
import com.mt.saga.infrastructure.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelRemoveOrderForInvalidEvent extends DomainEvent {
    public static final String name = Utility.getCancelLtxName(RemoveOrderForInvalidEvent.name);
    private String orderId;
    private String changeId;
    private long taskId;
    private Integer orderVersion;

    public CancelRemoveOrderForInvalidEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setOrderVersion(command.getVersion());
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT));
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }

}
