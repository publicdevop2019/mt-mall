package com.mt.saga.domain.model.cancel_update_order_address_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.invalid_order.event.IncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.infrastructure.AppConstant;
import com.mt.saga.infrastructure.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForUpdateOrderAddressEvent extends DomainEvent {
    public static final String name = Utility.getCancelLtxName(UpdateOrderForUpdateOrderAddressEvent.name);
    private String orderId;
    private String changeId;
    private long taskId;
    private Integer orderVersion;
    private BizOrderAddressCmdRep address;
    private long modifiedByUserAt;

    public CancelUpdateOrderForUpdateOrderAddressEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setDomainId(new DomainId(taskId.toString()));
        setOrderVersion(command.getVersion());
        setInternal(false);
        setName(name);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT));
        setAddress(command.getOriginalAddress());
        setModifiedByUserAt(command.getOriginalModifiedByUserAt());
    }
}
