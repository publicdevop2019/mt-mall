package com.mt.shop.domain.model.payment.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import lombok.Getter;

import static com.mt.shop.infrastructure.AppConstant.GENERATE_PAYMENT_QR_LINK_EVENT;
import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Getter
public class CancelPaymentQRLinkReplyEvent extends DomainEvent {
    public static final String name="CANCEL_PAYMENT_QR_LINK_REPLY_EVENT";
    private boolean emptyOpt;
    private long taskId;
    private String replyOf;

    public CancelPaymentQRLinkReplyEvent(boolean emptyOpt, long taskId) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        replyOf=MQHelper.cancelOf(GENERATE_PAYMENT_QR_LINK_EVENT);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
