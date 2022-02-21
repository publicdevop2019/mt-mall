package com.mt.shop.application.cart.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CancelClearCartForCreateCommand extends DomainEvent {
    private String userId;
    private Map<String,Integer> idVersionMap;
    private String changeId;
    private long taskId;

}
