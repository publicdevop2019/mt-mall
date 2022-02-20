package com.mt.saga.domain.model.cancel_create_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.saga.infrastructure.AppConstant.CANCEL_CREATE_ORDER_DTX_SUCCESS_EVENT;

@Setter
@Getter
@NoArgsConstructor
public class CancelCreateOrderDTXSuccessEvent extends DomainEvent {
    private String dtxId;
    private String changeId;
    public static final String name = "CANCEL_CREATE_ORDER_DTX_SUCCESS_EVENT";

    public CancelCreateOrderDTXSuccessEvent(CancelCreateOrderDTX dtx) {
        setInternal(true);
        setTopic(CANCEL_CREATE_ORDER_DTX_SUCCESS_EVENT);
        setDtxId(String.valueOf(dtx.getForwardDtxId()));
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setChangeId(dtx.getChangeId());
    }
}
