package com.mt.saga.domain.model.distributed_tx.event.create_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.create_order_dtx.command.GeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.infrastructure.AppConstant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveNewOrderEvent extends DomainEvent {
    public static final String name = "SAVE_NEW_ORDER_EVENT";
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

    public SaveNewOrderEvent(GeneratePaymentQRLinkReplyCommand reply, CommonOrderCommand command, Long taskId, String changeId) {
        setAddress(command.getAddress());
        setCreatedBy(command.getCreatedBy());
        setOrderId(command.getOrderId());
        setOrderState(BizOrderStatus.DRAFT);
        setPaymentAmt(command.getPaymentAmt());
        setPaymentType(command.getPaymentType());
        setPaymentLink(reply.getPaymentLink());
        setProductList(command.getProductList());
        setUserId(command.getUserId());
        setInternal(false);
        setTopic(AppConstant.SAVE_NEW_ORDER_FOR_CREATE_EVENT);
        setTaskId(taskId);
        setChangeId(changeId);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
