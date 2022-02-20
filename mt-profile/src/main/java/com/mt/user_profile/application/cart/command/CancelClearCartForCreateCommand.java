package com.mt.user_profile.application.cart.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
public class CancelClearCartForCreateCommand extends DomainEvent {
    private String userId;
    private Map<String,Integer> idVersionMap;
    private String changeId;
    private long taskId;

}
