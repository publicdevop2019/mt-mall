package com.mt.saga.domain.model.confirm_order_payment_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfirmOrderPaymentDTXFailedEvent extends DomainEvent {
    private ConfirmOrderPaymentDTX confirmOrderPaymentDTX;
    public static final String name = "CONFIRM_ORDER_PAYMENT_DTX_FAILED_EVENT";
    public ConfirmOrderPaymentDTXFailedEvent(ConfirmOrderPaymentDTX dtx) {
        this.confirmOrderPaymentDTX = dtx;
        setInternal(true);
        setTopic(AppConstant.CONFIRM_ORDER_PAYMENT_FAILED_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
