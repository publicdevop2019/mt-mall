package com.mt.shop.application.biz_order.representation;

import com.mt.shop.application.biz_order.command.CustomerPlaceBizOrderCommand;
import com.mt.shop.domain.biz_order.BizOrderSummary;
import com.mt.shop.domain.biz_order.CartDetail;
import com.mt.shop.domain.biz_order.ShippingDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerBizOrderRepresentation {
    private String id;
    private CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep address;

    private List<CartDetail> productList;

    private String paymentType;

    private BizOrderSummary.CustomerOrderStatus orderState;

    private BigDecimal paymentAmt;
    private String paymentLink;
    private boolean paid;
    private Integer version;

    public CustomerBizOrderRepresentation(BizOrderSummary customerOrder) {
        this.id = customerOrder.getOrderId().getDomainId();
        ShippingDetail address = customerOrder.getAddress();
        this.address = new CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep(address);

        this.productList = customerOrder.getProductList();
        this.paymentType = customerOrder.getPaymentType();
        this.paymentLink = customerOrder.getPaymentLink();
        this.paid = customerOrder.isPaid();
        this.orderState = customerOrder.getOrderStateForCustomer();
        this.version = customerOrder.getVersion();
    }
}
