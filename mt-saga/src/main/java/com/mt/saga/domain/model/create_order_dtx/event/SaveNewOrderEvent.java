package com.mt.saga.domain.model.create_order_dtx.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.create_order_dtx.command.GeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    public SaveNewOrderEvent(GeneratePaymentQRLinkReplyCommand reply, CreateOrderDTX dtx) {
        String createBizStateMachineCommand = dtx.getOrderCommandDetail();
        CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(createBizStateMachineCommand, CommonOrderCommand.class);
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
        setTaskId(dtx.getId());
        setChangeId(dtx.getChangeId());
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
