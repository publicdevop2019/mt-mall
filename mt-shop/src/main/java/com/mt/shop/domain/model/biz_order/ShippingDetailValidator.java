package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class ShippingDetailValidator {
    private ShippingDetail address;
    private ValidationNotificationHandler handler;

    public ShippingDetailValidator(ShippingDetail shippingDetail, ValidationNotificationHandler handler) {
        this.address = shippingDetail;
        this.handler = handler;
    }

    public void validate() {
        if (address.getLine2() != null && !address.getLine2().isBlank()) {
            if (address.getLine1() == null || address.getLine1().isBlank()) {
                handler.handleError("address line 1 should be filled first");
            }
        }
    }
}
