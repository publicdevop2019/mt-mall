package com.mt.user_profile.domain.biz_order;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class CartDetailValidator {
    private CartDetail cartDetail;
    private ValidationNotificationHandler handler;

    public CartDetailValidator(CartDetail cartDetail, ValidationNotificationHandler handler) {
        this.cartDetail = cartDetail;
        this.handler = handler;
    }
    public void validate(){

    }
}
