package com.mt.payment.domain.model.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import lombok.Data;

import static com.mt.payment.infrastructure.AppConstant.REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT;

@Data
public class RemovePaymentLinkReplyEvent extends DomainEvent {
    public static final String name="REMOVE_PAYMENT_LINK_REPLY_EVENT";
    private String taskId;
    private boolean emptyOpt;

    public RemovePaymentLinkReplyEvent(String taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(MQHelper.replyOf(REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
