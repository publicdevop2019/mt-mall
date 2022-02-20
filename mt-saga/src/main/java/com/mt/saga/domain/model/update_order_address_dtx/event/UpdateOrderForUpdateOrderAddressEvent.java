package com.mt.saga.domain.model.update_order_address_dtx.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderForUpdateOrderAddressEvent extends DomainEvent {
    public static final String name = "UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT";
    private String orderId;
    private String userId;
    private BizOrderStatus orderState;
    private String createdBy;
    private BizOrderAddressCmdRep address;
    private String changeId;
    private long taskId;
    private Integer orderVersion;

    public UpdateOrderForUpdateOrderAddressEvent(UpdateOrderAddressDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.changeId = dtx.getChangeId();
        this.taskId = dtx.getId();
        setOrderVersion(dtx.getOrderVersion());
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        CommonOrderCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getOrderCommandDetail(), CommonOrderCommand.class);
        setAddress(deserialize.getAddress());
        setUserId(deserialize.getUserId());
        setCreatedBy(deserialize.getCreatedBy());
    }
}
