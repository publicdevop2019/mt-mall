package com.mt.shop.application.biz_order.representation;

import com.mt.shop.domain.model.biz_order.BizOrderStatus;
import com.mt.shop.domain.model.biz_order.BizOrderSummary;
import com.mt.shop.domain.model.biz_order.CartDetail;
import com.mt.shop.domain.model.biz_order.ShippingDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
public class AdminBizOrderRepresentation {
    private String userId;
    private String id;

    private ShippingDetail address;
    private List<CartDetail> productList;
    private String paymentType;
    private String paymentLink;
    private BigDecimal paymentAmt;
    private String paymentDate;
    private boolean paid;
    private BizOrderStatus orderState;
    private long modifiedByUserAt;
    private String createdBy;
    private long createdAt;
    private String modifiedBy;
    private long modifiedAt;
    private Integer version;

    public AdminBizOrderRepresentation(BizOrderSummary bizOrder) {
        this.userId = bizOrder.getUserId();
        this.id = bizOrder.getOrderId().getDomainId();
        this.address = bizOrder.getAddress();
        this.productList = bizOrder.getProductList();
        this.paymentType = bizOrder.getPaymentType();
        this.paymentLink = bizOrder.getPaymentLink();
        this.paymentAmt = bizOrder.getPaymentAmt();
        this.paymentDate = bizOrder.getPaymentDate();
        this.paid = bizOrder.isPaid();
        this.orderState = bizOrder.getOrderState();
        this.createdBy = bizOrder.getCreatedBy();
        this.modifiedBy = bizOrder.getModifiedBy();
        this.version = bizOrder.getVersion();
        Optional.ofNullable(bizOrder.getModifiedByUserAt()).ifPresent(e -> {
            this.modifiedByUserAt = e.getTime();
        });
        this.createdAt = bizOrder.getCreatedAt().getTime();
        this.modifiedAt = bizOrder.getModifiedAt().getTime();
    }
}
