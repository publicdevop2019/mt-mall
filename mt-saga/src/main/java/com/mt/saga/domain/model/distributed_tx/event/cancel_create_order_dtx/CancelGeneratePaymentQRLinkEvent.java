package com.mt.saga.domain.model.distributed_tx.event.cancel_create_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.domain.model.distributed_tx.event.create_order_dtx.GeneratePaymentQRLinkEvent;
import com.mt.saga.infrastructure.AppConstant;
import com.mt.saga.infrastructure.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelGeneratePaymentQRLinkEvent extends DomainEvent {
    public static final String name = Utility.getCancelLtxName(GeneratePaymentQRLinkEvent.name);
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
