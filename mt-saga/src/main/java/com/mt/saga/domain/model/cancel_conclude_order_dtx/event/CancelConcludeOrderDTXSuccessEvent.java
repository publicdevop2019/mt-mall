package com.mt.saga.domain.model.cancel_conclude_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.saga.infrastructure.AppConstant.CANCEL_CONCLUDE_ORDER_DTX_SUCCESS_EVENT;

@Setter
@Getter
@NoArgsConstructor
public class CancelConcludeOrderDTXSuccessEvent extends DomainEvent {
    private String dtxId;
    private String changeId;
    public static final String name = "CANCEL_CONCLUDE_ORDER_SUCCESS_EVENT";
    public CancelConcludeOrderDTXSuccessEvent(CancelConcludeOrderDTX dtx) {
        setInternal(true);
        setTopic(CANCEL_CONCLUDE_ORDER_DTX_SUCCESS_EVENT);
        setDtxId(String.valueOf(dtx.getForwardDtxId()));
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setChangeId(dtx.getChangeId());
    }
}
