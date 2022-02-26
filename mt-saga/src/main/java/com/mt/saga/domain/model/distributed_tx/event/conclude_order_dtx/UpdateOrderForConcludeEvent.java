package com.mt.saga.domain.model.distributed_tx.event.conclude_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderForConcludeEvent extends DomainEvent {
    public static final String name = AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT;

    private String changeId;
    private long taskId;
    private String orderId;
    private Integer orderVersion;

    public UpdateOrderForConcludeEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT);
        setChangeId(changeId);
        setTaskId(taskId);
        setOrderId(orderId);
        setOrderVersion(command.getVersion());
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
