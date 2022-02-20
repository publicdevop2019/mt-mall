package com.mt.saga.domain.model.update_order_address_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderAddressDTXSuccessEvent extends DomainEvent {
    private String orderId;
    private long taskId;
    private String changeId;
    public static final String name = "UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT";
    public UpdateOrderAddressDTXSuccessEvent(UpdateOrderAddressDTX dtx) {
        setOrderId(dtx.getOrderId());
        setInternal(true);
        setTopic(AppConstant.UPDATE_ORDER_ADDRESS_DTX_SUCCESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setTaskId(dtx.getId());
        setChangeId(dtx.getChangeId());
    }
}
