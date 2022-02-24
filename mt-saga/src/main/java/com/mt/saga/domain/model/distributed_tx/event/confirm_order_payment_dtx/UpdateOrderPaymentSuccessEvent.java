package com.mt.saga.domain.model.distributed_tx.event.confirm_order_payment_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderPaymentSuccessEvent extends DomainEvent {
    public static final String name = AppConstant.UPDATE_ORDER_FOR_PAYMENT_SUCCESS_EVENT;
    private String orderId;
    private String userId;
    private BizOrderStatus orderState;
    private String createdBy;
    private BizOrderAddressCmdRep address;
    private List<CartDetail> productList;
    private String paymentType;
    private BigDecimal paymentAmt;
    private String paymentLink;
    private String changeId;
    private long taskId;
    private Integer orderVersion;

    public UpdateOrderPaymentSuccessEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.orderId = orderId;
        this.changeId = changeId;
        this.taskId = taskId;
        setOrderVersion(command.getVersion());
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_PAYMENT_SUCCESS_EVENT);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
