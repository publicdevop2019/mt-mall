package com.mt.saga.domain.model.cancel_create_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelGeneratePaymentQRLinkEvent extends DomainEvent {
    public static final String name = "CANCEL_GENERATE_PAYMENT_QR_LINK_EVENT";
    private String orderId;
    private String changeId;
    private long taskId;

    public CancelGeneratePaymentQRLinkEvent(String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setDomainId(new DomainId(taskId.toString()));
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT));
        setName(name);
    }
}
