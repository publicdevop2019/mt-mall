package com.mt.shop.domain.model.cart;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.shop.domain.DomainRegistry;
import org.springframework.stereotype.Service;

@Service
public class BizCartValidationService {
    public void validate(BizCart bizCart, ValidationNotificationHandler handler) {
        validateMaxCartItemPerUser(handler);
    }

    private void validateMaxCartItemPerUser(ValidationNotificationHandler handler) {
        SumPagedRep<BizCart> allCart = DomainRegistry.getBizCartRepository().cartItemOfQuery(new CartQuery());
        if (allCart.getData().size() == 10)
            handler.handleError("max cart item reached per user");
    }
}
