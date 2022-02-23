package com.mt.saga.domain.model.distributed_tx.event.cancel_create_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.distributed_tx.event.create_order_dtx.SaveNewOrderEvent;
import com.mt.saga.infrastructure.AppConstant;
import com.mt.saga.infrastructure.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelSaveNewOrderEvent extends DomainEvent {
    public static final String name = Utility.getCancelLtxName(SaveNewOrderEvent.name);
    private String orderId;
    private String userId;
    private String changeId;
    private long taskId;
    private int version;

    public CancelSaveNewOrderEvent(CommonOrderCommand command, String changeId, Long taskId) {
        setOrderId(command.getOrderId());
        setUserId(command.getUserId());
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.SAVE_NEW_ORDER_FOR_CREATE_EVENT));
        setTaskId(taskId);
        setChangeId(changeId);
        setDomainId(new DomainId(taskId.toString()));
        setVersion(0);//must be 0 for new order
        setName(name);
    }
}
