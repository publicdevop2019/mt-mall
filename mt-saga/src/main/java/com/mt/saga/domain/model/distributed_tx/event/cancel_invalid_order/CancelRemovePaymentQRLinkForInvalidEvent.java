package com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.domain.model.distributed_tx.event.invalid_order.RemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelRemovePaymentQRLinkForInvalidEvent extends DomainEvent {
    public static final String name = MQHelper.cancelOf(RemovePaymentQRLinkForInvalidEvent.name);
    private String orderId;
    private String changeId;
    private long taskId;

    public CancelRemovePaymentQRLinkForInvalidEvent(String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setInternal(false);
        setTopic(MQHelper.cancelOf(RemovePaymentQRLinkForInvalidEvent.name));
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
