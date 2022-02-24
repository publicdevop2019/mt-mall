package com.mt.shop.domain.model.address;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class AddressValidator {
    private Address address;
    private ValidationNotificationHandler handler;

    public void validate() {
        validateLine1AndLine2();
    }

    public AddressValidator(Address address, ValidationNotificationHandler handler) {
        this.address = address;
        this.handler = handler;
    }

    private void validateLine1AndLine2() {
        if (address.getLine2() != null && !address.getLine2().isBlank()) {
            if (address.getLine1() == null || address.getLine1().isBlank()) {
                handler.handleError("address line 1 should be filled first");
            }
        }
    }
}
