package com.mt.user_profile.application.biz_order.representation;

import com.mt.user_profile.domain.biz_order.BizOrderSummary;
import lombok.Data;

@Data
public class BizOrderPaymentLinkRepresentation {
    private String paymentLink;

    public BizOrderPaymentLinkRepresentation(BizOrderSummary bizOrder) {
        this.paymentLink = bizOrder.getPaymentLink();
    }
}
