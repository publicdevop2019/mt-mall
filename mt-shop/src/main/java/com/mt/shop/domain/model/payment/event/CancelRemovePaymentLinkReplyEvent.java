package com.mt.shop.domain.model.payment.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import lombok.Data;

import static com.mt.shop.infrastructure.AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT;

@Data
public class CancelRemovePaymentLinkReplyEvent extends DomainEvent {
    public static final String name = "CANCEL_REMOVE_PAYMENT_LINK_REPLY_EVENT";
    private String taskId;
    private boolean emptyOpt;

    public CancelRemovePaymentLinkReplyEvent(String taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(MQHelper.replyCancelOf(REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
