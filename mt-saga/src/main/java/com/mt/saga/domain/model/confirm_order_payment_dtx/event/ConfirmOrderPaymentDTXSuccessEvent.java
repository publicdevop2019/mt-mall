package com.mt.saga.domain.model.confirm_order_payment_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfirmOrderPaymentDTXSuccessEvent extends DomainEvent {
    private String orderId;
    private Integer orderVersion;
    private String changeId;
    private BizOrderStatus orderState;
    private String createBizStateMachineCommand;
    public static final String name = "CONFIRM_ORDER_PAYMENT_SUCCESS_EVENT";
    public ConfirmOrderPaymentDTXSuccessEvent(ConfirmOrderPaymentDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.orderState = BizOrderStatus.PAID_RESERVED;
        setChangeId(dtx.getChangeId());
        setCreateBizStateMachineCommand(dtx.getCreateBizStateMachineCommand());
        setOrderVersion(dtx.getOrderVersion()+1);
        setInternal(true);
        setTopic(AppConstant.CONFIRM_ORDER_PAYMENT_DTX_SUCCESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
