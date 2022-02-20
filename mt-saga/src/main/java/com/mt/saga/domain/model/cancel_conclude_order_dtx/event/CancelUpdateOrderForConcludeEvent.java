package com.mt.saga.domain.model.cancel_conclude_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
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
    public CancelUpdateOrderForConcludeEvent(CancelConcludeOrderDTX dtx) {
        setName(name);
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_CONCLUDE_EVENT));
        setChangeId(dtx.getChangeId());
        setTaskId(dtx.getId());
        setDomainId(new DomainId(dtx.getId().toString()));
        setOrderId(dtx.getOrderId());
        setOrderVersion(dtx.getOrderVersion());
    }
}
