package com.mt.saga.domain.model.conclude_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ConcludeOrderDTXSuccessEvent extends DomainEvent {
    private String orderId;
    private String changeId;
    public static final String name = "CONCLUDE_ORDER_SUCCESS_EVENT";
    public ConcludeOrderDTXSuccessEvent(ConcludeOrderDTX dtx) {
        this.orderId = dtx.getOrderId();
        setInternal(false);
        setTopic(AppConstant.CONCLUDE_ORDER_DTX_SUCCESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setChangeId(dtx.getChangeId());
    }
}
