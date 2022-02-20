package com.mt.saga.domain.model.cancel_update_order_address_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.saga.infrastructure.AppConstant.CANCEL_UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderAddressDTXSuccessEvent extends DomainEvent {
    public static final String name = "CANCEL_UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT";
    private String dtxId;
    private String changeId;

    public CancelUpdateOrderAddressDTXSuccessEvent(CancelUpdateOrderAddressDTX dtx) {
        setInternal(true);
        setTopic(CANCEL_UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT);
        setDtxId(String.valueOf(dtx.getForwardDtxId()));
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setChangeId(dtx.getChangeId());
    }
}
