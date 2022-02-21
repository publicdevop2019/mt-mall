package com.mt.shop.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.domain.biz_order.BizOrderId;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
public class InternalCancelNewOrderEvent extends DomainEvent {
    @TargetAggregateIdentifier
    private BizOrderId orderId;
    private String userId;
    private String changeId;
    private long taskId;
    private int version;

}
