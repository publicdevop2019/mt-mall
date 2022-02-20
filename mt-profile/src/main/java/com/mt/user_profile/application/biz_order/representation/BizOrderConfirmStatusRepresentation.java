package com.mt.user_profile.application.biz_order.representation;

import com.mt.user_profile.domain.biz_order.BizOrderSummary;
import lombok.Data;

@Data
public class BizOrderConfirmStatusRepresentation {
    private Boolean paymentStatus;

    public BizOrderConfirmStatusRepresentation(BizOrderSummary confirmPayment) {
        paymentStatus = confirmPayment.isPaid();
    }
}
