package com.mt.saga.domain.model.distributed_tx.event.update_order_address_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
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

    public UpdateOrderForUpdateOrderAddressEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setOrderVersion(command.getVersion());
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
        setAddress(command.getAddress());
        setUserId(command.getUserId());
        setCreatedBy(command.getCreatedBy());
    }
}
