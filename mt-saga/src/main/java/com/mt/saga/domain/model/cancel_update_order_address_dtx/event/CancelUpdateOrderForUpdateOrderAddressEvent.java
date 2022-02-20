package com.mt.saga.domain.model.cancel_update_order_address_dtx.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForUpdateOrderAddressEvent extends DomainEvent {
    public static final String name = "CANCEL_UPDATE_ORDER_ADDRESS_EVENT";
    private String orderId;
    private String changeId;
    private long taskId;
    private Integer orderVersion;
    private BizOrderAddressCmdRep address;
    private long modifiedByUserAt;
    public CancelUpdateOrderForUpdateOrderAddressEvent(CancelUpdateOrderAddressDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.changeId = dtx.getChangeId();
        this.taskId = dtx.getId();
        setDomainId(new DomainId(dtx.getId().toString()));
        setOrderVersion(dtx.getOrderVersion());
        setInternal(false);
        setName(name);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT));
        CommonOrderCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getCreateBizStateMachineCommand(), CommonOrderCommand.class);
        setAddress(deserialize.getOriginalAddress());
        setModifiedByUserAt(deserialize.getOriginalModifiedByUserAt());
    }
}
