package com.mt.shop.application.biz_order.representation;

import com.mt.shop.domain.biz_order.BizOrderSummary;
import com.mt.shop.domain.biz_order.CartDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerBizOrderCardRepresentation {
    private String id;
    private BigDecimal paymentAmt;
    private boolean paid;
    private BizOrderSummary.CustomerOrderStatus orderState;
    private List<CartDetail> productList;

    public CustomerBizOrderCardRepresentation(BizOrderSummary customerOrder1) {
        this.id = customerOrder1.getOrderId().getDomainId();
        this.paymentAmt = customerOrder1.getPaymentAmt();
        this.orderState = customerOrder1.getOrderStateForCustomer();
        this.paid = customerOrder1.isPaid();
        this.productList = customerOrder1.getProductList();
    }
}
