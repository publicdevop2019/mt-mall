package com.mt.user_profile.domain.cart;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class CartQueryValidator {
    private CartQuery cartQuery;
    private ValidationNotificationHandler handler;

    public CartQueryValidator(CartQuery cartQuery, ValidationNotificationHandler handler) {
        this.cartQuery = cartQuery;
        this.handler = handler;
    }
    public void validate(){
        validateUserIdMustPresent();
    }

    private void validateUserIdMustPresent() {
        if(cartQuery.getUserId()==null||cartQuery.getUserId().isBlank())
            handler.handleError("user id must present for cart query");
    }
}
