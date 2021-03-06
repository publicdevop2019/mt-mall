package com.mt.saga.domain.model.distributed_tx.event.cancel_update_order_address_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.distributed_tx.event.update_order_address_dtx.UpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForUpdateOrderAddressEvent extends DomainEvent {
    public static final String name = MQHelper.cancelOf(UpdateOrderForUpdateOrderAddressEvent.name);
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
        setTopic(MQHelper.cancelOf(UpdateOrderForUpdateOrderAddressEvent.name));
        setAddress(command.getOriginalAddress());
        setModifiedByUserAt(command.getOriginalModifiedByUserAt());
    }
}
