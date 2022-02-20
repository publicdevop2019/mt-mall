package com.mt.saga.domain.model.cancel_create_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelSaveNewOrderEvent extends DomainEvent {
    public static final String name = "CANCEL_SAVE_NEW_ORDER_EVENT";
    private String orderId;
    private String userId;
    private String changeId;
    private long taskId;
    private int version;

    public CancelSaveNewOrderEvent(CommonOrderCommand command, CancelCreateOrderDTX dtx) {
        setOrderId(command.getOrderId());
        setUserId(command.getUserId());
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.SAVE_NEW_ORDER_FOR_CREATE_EVENT));
        setTaskId(dtx.getId());
        setChangeId(dtx.getChangeId());
        setDomainId(new DomainId(dtx.getId().toString()));
        setVersion(0);//must be 0 for new order
        setName(name);
    }
}