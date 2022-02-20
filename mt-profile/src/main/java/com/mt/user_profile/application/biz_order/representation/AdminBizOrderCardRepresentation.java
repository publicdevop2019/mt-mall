package com.mt.user_profile.application.biz_order.representation;

import com.mt.user_profile.domain.biz_order.CartDetail;
import com.mt.user_profile.domain.biz_order.BizOrderStatus;
import com.mt.user_profile.domain.biz_order.BizOrderSummary;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminBizOrderCardRepresentation {
    private String id;
    private BigDecimal paymentAmt;
    private BizOrderStatus orderState;
    private List<CartDetail> productList;
    private long createdAt;
    private String userId;
    private String createdBy;
    private Integer version;

    public AdminBizOrderCardRepresentation(BizOrderSummary bizOrder1) {
        this.id = bizOrder1.getOrderId().getDomainId();
        this.paymentAmt = bizOrder1.getPaymentAmt();
        this.orderState = bizOrder1.getOrderState();
        this.productList = bizOrder1.getProductList();
        this.createdAt = bizOrder1.getCreatedAt().getTime();
        this.userId = bizOrder1.getUserId();
        this.createdBy = bizOrder1.getCreatedBy();
        this.version = bizOrder1.getVersion();
    }
}
