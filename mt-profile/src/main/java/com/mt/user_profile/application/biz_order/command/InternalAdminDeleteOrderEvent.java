package com.mt.user_profile.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.user_profile.domain.biz_order.BizOrderId;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
public class InternalAdminDeleteOrderEvent extends DomainEvent {
    @TargetAggregateIdentifier
    private BizOrderId orderId;
    private String userId;
    private String changeId;
    private int version;

}
