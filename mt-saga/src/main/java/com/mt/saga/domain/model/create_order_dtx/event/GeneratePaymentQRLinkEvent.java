package com.mt.saga.domain.model.create_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GeneratePaymentQRLinkEvent extends DomainEvent {
    public static final String name = "GENERATE_PAYMENT_QR_LINK_EVENT";
    private String orderId;
    private String changeId;
    private long taskId;

    public GeneratePaymentQRLinkEvent(CreateOrderDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.changeId = dtx.getChangeId();
        this.taskId = dtx.getId();
        setInternal(false);
        setTopic(AppConstant.GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
