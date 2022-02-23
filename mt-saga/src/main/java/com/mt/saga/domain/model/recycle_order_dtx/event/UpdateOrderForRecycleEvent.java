package com.mt.saga.domain.model.recycle_order_dtx.event;

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
public class UpdateOrderForRecycleEvent extends DomainEvent {
    public static final String name = "UPDATE_ORDER_FOR_RECYCLE_EVENT";
    private String changeId;
    private long taskId;
    private String orderId;
    private Integer orderVersion;

    public UpdateOrderForRecycleEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT);
        setChangeId(changeId);
        setTaskId(taskId);
        setOrderId(orderId);
        setOrderVersion(command.getVersion());
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
