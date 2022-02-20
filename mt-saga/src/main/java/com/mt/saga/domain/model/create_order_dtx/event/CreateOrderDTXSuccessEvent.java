package com.mt.saga.domain.model.create_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateOrderDTXSuccessEvent extends DomainEvent {
    private String orderId;
    private long taskId;
    private String changeId;
    public static final String name = "CREATE_ORDER_DTX_SUCCESS_EVENT";
    public CreateOrderDTXSuccessEvent(CreateOrderDTX dtx) {
        setOrderId(dtx.getOrderId());
        setInternal(true);
        setTopic(AppConstant.CREATE_ORDER_DTX_SUCCESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setTaskId(dtx.getId());
        setChangeId(dtx.getChangeId());
    }
}
