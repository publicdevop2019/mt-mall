package com.mt.shop.domain.model.payment.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Data;

import static com.mt.shop.infrastructure.AppConstant.GENERATE_PAYMENT_QR_LINK_EVENT;
import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Data
public class GeneratePaymentLinkReplyEvent extends DomainEvent {
    public static final String name = "GENERATE_PAYMENT_LINK_REPLY_EVENT";
    private String paymentLink;
    private String taskId;
    private boolean emptyOpt;
    private String replyOf;

    public GeneratePaymentLinkReplyEvent(String paymentLink, String taskId, boolean emptyOpt) {
        this.paymentLink = paymentLink;
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf = GENERATE_PAYMENT_QR_LINK_EVENT;
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
