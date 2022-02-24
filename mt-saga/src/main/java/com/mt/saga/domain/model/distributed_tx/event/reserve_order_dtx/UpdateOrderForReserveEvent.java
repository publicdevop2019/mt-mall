package com.mt.saga.domain.model.distributed_tx.event.reserve_order_dtx;

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
public class UpdateOrderForReserveEvent extends DomainEvent {
    public static final String name = AppConstant.UPDATE_ORDER_FOR_RESERVE_EVENT;
    private String orderId;
    private long taskId;
    private String changeId;
    private Integer orderVersion;

    public UpdateOrderForReserveEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.taskId = taskId;
        this.changeId =changeId;
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_RESERVE_EVENT);
        setOrderVersion(command.getVersion());
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
