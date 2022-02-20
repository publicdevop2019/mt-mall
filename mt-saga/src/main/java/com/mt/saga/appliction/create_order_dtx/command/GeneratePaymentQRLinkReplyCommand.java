package com.mt.saga.appliction.create_order_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;

@Getter
public class GeneratePaymentQRLinkReplyCommand extends DomainEvent {
    private long taskId;
    private String paymentLink;
    private boolean emptyOpt;
}
