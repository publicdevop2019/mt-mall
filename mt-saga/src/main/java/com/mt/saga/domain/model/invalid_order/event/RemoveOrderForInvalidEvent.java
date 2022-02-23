package com.mt.saga.domain.model.invalid_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RemoveOrderForInvalidEvent extends DomainEvent {
    public static final String name = "REMOVE_ORDER_FOR_INVALID_EVENT";
    private String orderId;
    private String changeId;
    private String userId;
    private long taskId;
    private Integer orderVersion;

    public RemoveOrderForInvalidEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setOrderVersion(command.getVersion());
        setInternal(false);
        setTopic(AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
        setUserId(command.getUserId());
    }

}
