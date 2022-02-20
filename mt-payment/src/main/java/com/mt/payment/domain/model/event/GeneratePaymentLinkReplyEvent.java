package com.mt.payment.domain.model.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GeneratePaymentLinkReplyEvent extends DomainEvent {
    public static final String name="GENERATE_PAYMENT_LINK_REPLY_EVENT";
    private String paymentLink;
    private String taskId;
    private boolean emptyOpt;

    public GeneratePaymentLinkReplyEvent(String paymentLink, String taskId,boolean emptyOpt) {
        this.paymentLink = paymentLink;
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic("generate_payment_qr_link_reply_event");
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
