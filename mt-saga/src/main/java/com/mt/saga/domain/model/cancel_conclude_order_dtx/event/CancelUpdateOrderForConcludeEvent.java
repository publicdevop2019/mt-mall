package com.mt.saga.domain.model.cancel_conclude_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelUpdateOrderForConcludeEvent extends DomainEvent {
    public static final String name = "CANCEL_UPDATE_ORDER_FOR_CONCLUDE_EVENT";
    private String changeId;
    private long taskId;
    private String orderId;
    private Integer orderVersion;

    public CancelUpdateOrderForConcludeEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        setName(name);
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT));
        setChangeId(changeId);
        setTaskId(taskId);
        setDomainId(new DomainId(taskId.toString()));
        setOrderId(orderId);
        setOrderVersion(command.getVersion());
    }
}
