package com.mt.user_profile.application.cart.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ClearCartForCreateEvent extends DomainEvent {
    private String userId;
    private Set<String> ids;
    private String changeId;
    private long taskId;

}
