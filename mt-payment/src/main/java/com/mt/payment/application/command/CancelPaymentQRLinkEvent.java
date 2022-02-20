package com.mt.payment.application.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelPaymentQRLinkEvent extends DomainEvent {
    private String orderId;
    private String changeId;
    private long taskId;
}
