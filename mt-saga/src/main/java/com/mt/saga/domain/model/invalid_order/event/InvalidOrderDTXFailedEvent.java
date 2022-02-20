package com.mt.saga.domain.model.invalid_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InvalidOrderDTXFailedEvent extends DomainEvent {
    private InvalidOrderDTX dtx;
    public static final String name = "INVALID_ORDER_DTX_FAILED_EVENT";
    public InvalidOrderDTXFailedEvent(InvalidOrderDTX dtx) {
        this.dtx = dtx;
        setInternal(true);
        setTopic(AppConstant.INVALID_ORDER_DTX_FAILED_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
