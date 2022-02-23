package com.mt.saga.domain.model.distributed_tx.event.cancel_recycle_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.recycle_order_dtx.event.UpdateOrderForRecycleEvent;
import com.mt.saga.infrastructure.AppConstant;
import com.mt.saga.infrastructure.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForRecycleEvent extends DomainEvent {
    public static final String name = Utility.getCancelLtxName(UpdateOrderForRecycleEvent.name);
    private String changeId;
    private long taskId;
    private String orderId;
    private Integer orderVersion;

    public CancelUpdateOrderForRecycleEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT));
        setChangeId(changeId);
        setTaskId(taskId);
        setOrderId(orderId);
        setOrderVersion(command.getVersion());
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
