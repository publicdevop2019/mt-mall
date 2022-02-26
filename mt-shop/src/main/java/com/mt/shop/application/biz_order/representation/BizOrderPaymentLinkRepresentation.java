package com.mt.shop.application.biz_order.representation;

import com.mt.shop.domain.model.biz_order.BizOrderSummary;
import lombok.Data;

@Data
public class BizOrderPaymentLinkRepresentation {
    private String paymentLink;

    public BizOrderPaymentLinkRepresentation(BizOrderSummary bizOrder) {
        this.paymentLink = bizOrder.getPaymentLink();
    }
}
