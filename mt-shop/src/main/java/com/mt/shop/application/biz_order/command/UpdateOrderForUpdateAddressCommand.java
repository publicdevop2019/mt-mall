package com.mt.shop.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.domain.model.biz_order.BizOrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderForUpdateAddressCommand extends DomainEvent {
    private String orderId;
    private String userId;
    private BizOrderStatus orderState;
    private String createdBy;
    private CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep address;
    private String changeId;
    private long taskId;
    private Integer orderVersion;
}
