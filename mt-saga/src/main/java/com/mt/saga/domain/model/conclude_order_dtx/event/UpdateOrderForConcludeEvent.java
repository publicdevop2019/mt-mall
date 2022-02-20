package com.mt.saga.domain.model.conclude_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderForConcludeEvent extends DomainEvent {
    public static final String name = "UPDATE_ORDER_FOR_CONCLUDE_EVENT";
    private String changeId;
    private long taskId;
    private String orderId;
    private Integer orderVersion;
    public UpdateOrderForConcludeEvent(ConcludeOrderDTX dtx) {
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT);
        setChangeId(dtx.getChangeId());
        setTaskId(dtx.getId());
        setOrderId(dtx.getOrderId());
        setOrderVersion(dtx.getOrderVersion());
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
