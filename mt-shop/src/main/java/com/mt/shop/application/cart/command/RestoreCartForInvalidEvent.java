package com.mt.shop.application.cart.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RestoreCartForInvalidEvent extends DomainEvent {
    private String userId;
    private Set<String> ids;
    private String changeId;
    private long taskId;
    private Map<String,Integer> idVersionMap;
}
