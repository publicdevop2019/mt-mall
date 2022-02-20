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
public class CreateOrderDTXFailedEvent extends DomainEvent {
    private CreateOrderDTX createOrderDTX;
    public static final String name = "CREATE_ORDER_DTX_FAILED_EVENT";
    public CreateOrderDTXFailedEvent(CreateOrderDTX dtx) {
        this.createOrderDTX = dtx;
        setInternal(true);
        setTopic(AppConstant.CREATE_ORDER_DTX_FAILED_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
