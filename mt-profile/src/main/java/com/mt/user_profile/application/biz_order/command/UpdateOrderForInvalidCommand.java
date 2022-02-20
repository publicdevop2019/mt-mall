package com.mt.user_profile.application.biz_order.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.user_profile.domain.biz_order.BizOrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderForInvalidCommand extends DomainEvent {
    private String orderId;
    private String changeId;
    private String userId;
    private long taskId;
    private Integer orderVersion;
}
