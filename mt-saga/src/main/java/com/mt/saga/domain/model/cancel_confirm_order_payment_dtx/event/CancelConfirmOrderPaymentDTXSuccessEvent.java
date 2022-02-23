package com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.saga.infrastructure.AppConstant.CANCEL_CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT;

@Setter
@Getter
@NoArgsConstructor
public class CancelConfirmOrderPaymentDTXSuccessEvent extends DomainEvent {
    public static final String name = "CANCEL_CONFIRM_ORDER_PAYMENT_SUCCESS_EVENT";
    private String dtxId;
    private String changeId;

    public CancelConfirmOrderPaymentDTXSuccessEvent(CancelConfirmOrderPaymentDTX dtx) {
        setInternal(true);
        setTopic(CANCEL_CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT);
        setDtxId(String.valueOf(dtx.getForwardDtxId()));
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setChangeId(dtx.getChangeId());
    }
}
