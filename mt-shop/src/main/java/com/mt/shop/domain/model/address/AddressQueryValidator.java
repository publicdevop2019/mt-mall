package com.mt.shop.domain.model.address;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class AddressQueryValidator {
    private AddressQuery addressQuery;
    private ValidationNotificationHandler handler;

    public AddressQueryValidator(AddressQuery addressQuery, ValidationNotificationHandler handler) {
        this.addressQuery = addressQuery;
        this.handler = handler;
    }

    public void validate() {
        validateNonAdminQueryMustHaveUserId();
    }

    private void validateNonAdminQueryMustHaveUserId() {
        if (!addressQuery.isAdmin() && (addressQuery.getUserId() == null || addressQuery.getUserId().isBlank())) {
            handler.handleError("user id must present when non admin query");
        }
    }
}
