package com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.conclude_order_dtx.event.UpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.UpdateOrderPaymentSuccessEvent;
import com.mt.saga.infrastructure.AppConstant;
import com.mt.saga.infrastructure.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderPaymentEvent extends DomainEvent {
    public static final String name = Utility.getCancelLtxName(UpdateOrderPaymentSuccessEvent.name);
    private String orderId;
    private String changeId;
    private long taskId;
    private Integer orderVersion;

    public CancelUpdateOrderPaymentEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setDomainId(new DomainId(taskId.toString()));
        setOrderVersion(command.getVersion());
        setInternal(false);
        setName(name);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_PAYMENT_SUCCESS_EVENT));
    }
}
