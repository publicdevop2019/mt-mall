package com.mt.shop.domain.model.payment.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;

@Getter
public class CancelPaymentQRLinkReplyEvent extends DomainEvent {
    public static final String name="CANCEL_PAYMENT_QR_LINK_REPLY_EVENT";
    private boolean emptyOpt;
    private long taskId;

    public CancelPaymentQRLinkReplyEvent(boolean emptyOpt, long taskId) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic("cancel_generate_payment_qr_link_reply_event");
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
