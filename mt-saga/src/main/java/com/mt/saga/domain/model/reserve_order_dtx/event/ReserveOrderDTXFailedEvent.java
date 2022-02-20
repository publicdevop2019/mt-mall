package com.mt.saga.domain.model.reserve_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReserveOrderDTXFailedEvent extends DomainEvent {
    public static final String name = "RESERVE_ORDER_DTX_FAILED_EVENT";
    private ReserveOrderDTX reserveOrderDTX;

    public ReserveOrderDTXFailedEvent(ReserveOrderDTX dtx) {
        this.reserveOrderDTX = dtx;
        setInternal(true);
        setTopic(AppConstant.RESERVE_ORDER_DTX_FAILED_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
