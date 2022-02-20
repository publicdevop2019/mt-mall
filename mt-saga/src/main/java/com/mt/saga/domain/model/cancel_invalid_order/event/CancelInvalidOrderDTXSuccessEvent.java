package com.mt.saga.domain.model.cancel_invalid_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.saga.infrastructure.AppConstant.CANCEL_INVALID_ORDER_DTX_SUCCESS_EVENT;

@Setter
@Getter
@NoArgsConstructor
public class CancelInvalidOrderDTXSuccessEvent extends DomainEvent {
    public static final String name = "CANCEL_INVALID_ORDER_DTX_SUCCESS_EVENT";
    private String dtxId;
    private String changeId;

    public CancelInvalidOrderDTXSuccessEvent(CancelInvalidOrderDTX dtx) {
        setInternal(true);
        setTopic(CANCEL_INVALID_ORDER_DTX_SUCCESS_EVENT);
        setDtxId(String.valueOf(dtx.getForwardDtxId()));
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setChangeId(dtx.getChangeId());
    }
}
