package com.mt.saga.appliction.cancel_create_order_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;

@Getter
public class CancelGeneratePaymentQRLinkReplyCommand extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
}
