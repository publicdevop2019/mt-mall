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
public class RecycleOrderDTXFailedEvent extends DomainEvent {
    private RecycleOrderDTX recycleOrderDTX;
    public static final String name = "RECYCLE_ORDER_DTX_FAILED_EVENT";
    public RecycleOrderDTXFailedEvent(RecycleOrderDTX dtx) {
        this.recycleOrderDTX = dtx;
        setInternal(true);
        setTopic(AppConstant.RECYCLE_ORDER_DTX_FAILED_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
