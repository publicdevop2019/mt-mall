package com.mt.shop.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.domain.model.biz_order.BizOrderId;
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
