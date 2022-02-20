package com.mt.user_profile.application;

import com.mt.common.domain.model.idempotent.IdempotentService;
import com.mt.user_profile.application.address.AddressApplicationService;
import com.mt.user_profile.application.biz_order.BizOrderApplicationService;
import com.mt.user_profile.application.cart.CartApplicationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Getter
    private static BizOrderApplicationService bizOrderApplicationService;
    @Getter
    private static IdempotentService idempotentService;
    @Getter
    private static AddressApplicationService addressApplicationService;
    @Getter
    private static CartApplicationService cartApplicationService;

    @Autowired
    private void setCartApplicationService(CartApplicationService cartApplicationService) {
        ApplicationServiceRegistry.cartApplicationService = cartApplicationService;
    }

    @Autowired
    private void setBizOrderApplicationService(BizOrderApplicationService bizOrderApplicationService) {
        ApplicationServiceRegistry.bizOrderApplicationService = bizOrderApplicationService;
    }

    @Autowired
    private void setIdempotentService(IdempotentService idempotentService) {
        ApplicationServiceRegistry.idempotentService = idempotentService;
    }

    @Autowired
    private void setAddressApplicationService(AddressApplicationService addressApplicationService) {
        ApplicationServiceRegistry.addressApplicationService = addressApplicationService;
    }
}
