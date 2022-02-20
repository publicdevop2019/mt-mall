package com.mt.saga.appliction.update_order_address_dtx.command;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderUpdateForAddressUpdateFailedCommand extends DomainEvent {
    private long taskId;
}
