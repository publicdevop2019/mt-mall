package com.mt.saga.domain.model.distributed_tx.event.cancel_confirm_order_payment_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.distributed_tx.event.confirm_order_payment_dtx.UpdateOrderPaymentSuccessEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderPaymentEvent extends DomainEvent {
    public static final String name = MQHelper.cancelOf(UpdateOrderPaymentSuccessEvent.name);
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
        setTopic(MQHelper.cancelOf(UpdateOrderPaymentSuccessEvent.name));
    }
}
