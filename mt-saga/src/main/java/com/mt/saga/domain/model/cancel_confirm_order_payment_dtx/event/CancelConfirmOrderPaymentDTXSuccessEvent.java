package com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mt.saga.infrastructure.AppConstant.CANCEL_CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT;
import static com.mt.saga.infrastructure.AppConstant.CANCEL_CREATE_ORDER_DTX_SUCCESS_EVENT;

@Setter
@Getter
@NoArgsConstructor
public class CancelConfirmOrderPaymentDTXSuccessEvent extends DomainEvent {
    private String dtxId;
    private String changeId;
    public static final String name = "CANCEL_CONFIRM_ORDER_PAYMENT_SUCCESS_EVENT";

    public CancelConfirmOrderPaymentDTXSuccessEvent(CancelConfirmOrderPaymentDTX dtx) {
        setInternal(true);
        setTopic(CANCEL_CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT);
        setDtxId(String.valueOf(dtx.getForwardDtxId()));
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setChangeId(dtx.getChangeId());
    }
}
