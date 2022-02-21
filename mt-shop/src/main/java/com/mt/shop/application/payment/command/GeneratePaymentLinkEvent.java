package com.mt.shop.application.payment.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Data;

@Data
public class GeneratePaymentLinkEvent extends DomainEvent {
    private static final long serialVersionUID = 1;
    private String userId;

    private String orderId;

    private String changeId;

    private String taskId;

    private String prepayId;

}
