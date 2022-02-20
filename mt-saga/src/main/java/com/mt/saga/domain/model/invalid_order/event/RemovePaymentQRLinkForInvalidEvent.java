package com.mt.saga.domain.model.invalid_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RemovePaymentQRLinkForInvalidEvent extends DomainEvent {
    public static final String name = "REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT";
    private String orderId;
    private String changeId;
    private long taskId;

    public RemovePaymentQRLinkForInvalidEvent(InvalidOrderDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.changeId = dtx.getChangeId();
        this.taskId = dtx.getId();
        setInternal(false);
        setTopic(AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
