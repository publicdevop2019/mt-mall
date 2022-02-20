package com.mt.saga.domain.model.update_order_address_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderAddressDTXFailedEvent extends DomainEvent {
    public static final String name = "UPDATE_ORDER_ADDRESS_DTX_FAILED_EVENT";
    private UpdateOrderAddressDTX dtx;

    public UpdateOrderAddressDTXFailedEvent(UpdateOrderAddressDTX dtx) {
        this.dtx = dtx;
        setInternal(true);
        setTopic(AppConstant.UPDATE_ORDER_ADDRESS_DTX_FAILED_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
