package com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderPaymentEvent extends DomainEvent {
    public static final String name = "CANCEL_UPDATE_ORDER_PAYMENT_SUCCESS_EVENT";
    private String orderId;
    private String changeId;
    private long taskId;
    private Integer orderVersion;

    public CancelUpdateOrderPaymentEvent(CancelConfirmOrderPaymentDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.changeId = dtx.getChangeId();
        this.taskId = dtx.getId();
        setDomainId(new DomainId(dtx.getId().toString()));
        setOrderVersion(dtx.getOrderVersion());
        setInternal(false);
        setName(name);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_PAYMENT_SUCCESS_EVENT));
    }
}
