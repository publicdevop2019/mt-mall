package com.mt.saga.appliction.cancel_update_order_address_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderAddressReplyEvent extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
}
