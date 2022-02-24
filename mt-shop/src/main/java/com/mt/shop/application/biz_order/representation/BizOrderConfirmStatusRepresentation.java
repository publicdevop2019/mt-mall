package com.mt.shop.application.biz_order.representation;

import com.mt.shop.domain.model.biz_order.BizOrderSummary;
import lombok.Data;

@Data
public class BizOrderConfirmStatusRepresentation {
    private Boolean paymentStatus;

    public BizOrderConfirmStatusRepresentation(BizOrderSummary confirmPayment) {
        paymentStatus = confirmPayment.isPaid();
    }
}
