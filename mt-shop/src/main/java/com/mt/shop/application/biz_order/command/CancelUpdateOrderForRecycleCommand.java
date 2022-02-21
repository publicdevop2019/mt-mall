package com.mt.shop.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForRecycleCommand extends DomainEvent {
    private String changeId;
    private String orderId;
    private int orderVersion;
    private long taskId;
}
