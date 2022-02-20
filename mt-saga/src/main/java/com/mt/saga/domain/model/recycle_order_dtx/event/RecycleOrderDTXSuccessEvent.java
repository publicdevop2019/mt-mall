package com.mt.saga.domain.model.recycle_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecycleOrderDTXSuccessEvent extends DomainEvent {
    private String orderId;
    private long taskId;
    private String changeId;
    public static final String name = "RECYCLE_ORDER_DTX_SUCCESS_EVENT";
    public RecycleOrderDTXSuccessEvent(RecycleOrderDTX dtx) {
        setOrderId(dtx.getOrderId());
        setInternal(true);
        setTopic(AppConstant.RECYCLE_ORDER_DTX_SUCCESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setTaskId(dtx.getId());
        setChangeId(dtx.getChangeId());
    }
}
