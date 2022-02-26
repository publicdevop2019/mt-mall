package com.mt.shop.domain.model.biz_order.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.application.biz_order.command.CustomerPlaceBizOrderCommand;
import com.mt.shop.domain.model.biz_order.BizOrderEvent;
import com.mt.shop.domain.model.biz_order.BizOrderStatus;
import com.mt.shop.domain.model.biz_order.CartDetail;
import com.mt.shop.domain.model.biz_order.ShippingDetail;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderOperationEvent extends DomainEvent {
    public static final String name = "ORDER_OPERATION_EVENT";
    private String orderId;
    private String userId;
    private String txId;
    private BizOrderStatus orderState;
    private BizOrderEvent bizOrderEvent;
    private List<CartDetail> productList;
    private String createdBy;
    private CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep address;
    private CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep originalAddress;//for cancel purpose
    private long originalModifiedByUserAt;//for cancel purpose
    private String paymentType;
    private BigDecimal paymentAmt;
    private Integer version;

    public OrderOperationEvent() {
        setInternal(false);
        setTopic(AppConstant.ORDER_OPERATION_EVENT);
        setName(name);
    }

    public void setOriginalAddress(ShippingDetail address) {
        this.originalAddress = new CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep(address);
    }
}
