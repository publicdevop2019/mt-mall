package com.mt.saga.domain.model.invalid_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InvalidOrderDTXSuccessEvent extends DomainEvent {
    private String orderId;
    private long taskId;
    private String changeId;
    public static final String name = "INVALID_ORDER_DTX_SUCCESS_EVENT";
    public InvalidOrderDTXSuccessEvent(InvalidOrderDTX dtx) {
        setOrderId(dtx.getOrderId());
        setInternal(true);
        setTopic(AppConstant.INVALID_ORDER_DTX_SUCCESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setTaskId(dtx.getId());
        setChangeId(dtx.getChangeId());
    }
}
