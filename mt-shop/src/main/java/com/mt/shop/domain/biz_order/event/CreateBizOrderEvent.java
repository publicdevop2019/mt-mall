package com.mt.shop.domain.biz_order.event;

import com.mt.shop.application.biz_order.command.InternalCreateNewOrderCommand;
import com.mt.shop.domain.biz_order.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Setter(AccessLevel.PRIVATE)
public class CreateBizOrderEvent implements Serializable {
    private static final long serialVersionUID = 1;
    private BizOrderId orderId;
    private String userId;
    private String changeId;
    private BizOrderStatus orderState;
    private String createdBy;
    private ShippingDetail address;
    private List<CartDetail> productList;
    private PaymentDetail.PaymentType paymentType;
    private BigDecimal paymentAmt;
    private String paymentLink;
    private Date modifiedByUserAt;

    public CreateBizOrderEvent(InternalCreateNewOrderCommand command) {
        orderId = command.getOrderId();
        userId = command.getUserId();
        orderState = BizOrderStatus.DRAFT;
        createdBy = command.getCreatedBy();
        address = new ShippingDetail(command.getAddress());
        productList = command.getProductList().stream().map(CartDetail::new).collect(Collectors.toList());
        paymentType = command.getPaymentType();
        paymentAmt = command.getPaymentAmt();
        paymentLink = command.getPaymentLink();
        changeId = command.getChangeId();
        modifiedByUserAt = Date.from(Instant.now());
    }
}
