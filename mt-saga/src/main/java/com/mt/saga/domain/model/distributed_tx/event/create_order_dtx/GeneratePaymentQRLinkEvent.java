package com.mt.saga.domain.model.distributed_tx.event.create_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
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

    public GeneratePaymentQRLinkEvent(String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
