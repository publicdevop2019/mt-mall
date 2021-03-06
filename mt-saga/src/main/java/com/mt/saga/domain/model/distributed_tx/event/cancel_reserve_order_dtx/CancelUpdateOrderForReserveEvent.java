package com.mt.saga.domain.model.distributed_tx.event.cancel_reserve_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.distributed_tx.event.reserve_order_dtx.UpdateOrderForReserveEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelUpdateOrderForReserveEvent extends DomainEvent {
    public static final String name = MQHelper.cancelOf(UpdateOrderForReserveEvent.name);
    private String changeId;
    private long taskId;
    private Integer orderVersion;
    private String orderId;

    public CancelUpdateOrderForReserveEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.changeId = changeId;
        this.taskId = taskId;
        setInternal(false);
        setDomainId(new DomainId(taskId.toString()));
        setTopic(MQHelper.cancelOf(UpdateOrderForReserveEvent.name));
        setOrderId(orderId);
        setOrderVersion(command.getVersion());
        setName(name);
    }
}
