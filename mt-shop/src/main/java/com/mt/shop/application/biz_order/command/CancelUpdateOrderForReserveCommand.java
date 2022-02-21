package com.mt.shop.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForReserveCommand extends DomainEvent {
    private String changeId;
    private long taskId;
    private String orderId;
    private int orderVersion;
}
