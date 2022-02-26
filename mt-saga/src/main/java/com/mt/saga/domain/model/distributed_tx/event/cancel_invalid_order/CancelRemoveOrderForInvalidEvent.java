package com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.distributed_tx.event.invalid_order.RemoveOrderForInvalidEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelRemoveOrderForInvalidEvent extends DomainEvent {
    public static final String name = MQHelper.cancelOf(RemoveOrderForInvalidEvent.name);
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
        setTopic(MQHelper.cancelOf(RemoveOrderForInvalidEvent.name));
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }

}
