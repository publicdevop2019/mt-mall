package com.mt.user_profile.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.user_profile.domain.biz_order.BizOrderId;
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
