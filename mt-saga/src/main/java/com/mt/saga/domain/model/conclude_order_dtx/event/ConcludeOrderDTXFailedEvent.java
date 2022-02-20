package com.mt.saga.domain.model.conclude_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConcludeOrderDTXFailedEvent extends DomainEvent {
    private ConcludeOrderDTX concludeOrderDTX;
    public static final String name = "CONCLUDE_ORDER_DTX_FAILED_EVENT";
    public ConcludeOrderDTXFailedEvent(ConcludeOrderDTX dtx) {
        setInternal(true);
        setTopic(AppConstant.CONCLUDE_ORDER_DTX_FAILED_EVENT);
        this.concludeOrderDTX = dtx;
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
