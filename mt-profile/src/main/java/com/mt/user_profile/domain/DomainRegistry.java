package com.mt.user_profile.domain;

import com.mt.user_profile.domain.address.AddressRepository;
import com.mt.user_profile.domain.address.AddressValidationService;
import com.mt.user_profile.domain.biz_order.BizOderSummaryRepository;
import com.mt.user_profile.domain.biz_order.BizOrderService;
import com.mt.user_profile.domain.cart.BizCartRepository;
import com.mt.user_profile.domain.cart.BizCartValidationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static BizOderSummaryRepository bizOderSummaryRepository;
    @Getter
    private static BizOrderService bizOrderService;
    @Getter
    private static AddressRepository addressRepository;
    @Getter
    private static BizCartRepository bizCartRepository;
    @Getter
    private static AddressValidationService addressValidationService;
    @Getter
    private static BizCartValidationService bizCartValidationService;

    @Autowired
    private void setBizCartRepository(BizCartRepository bizCartRepository) {
        DomainRegistry.bizCartRepository = bizCartRepository;
    }

    @Autowired
    private void setBizCartValidationService(BizCartValidationService bizCartValidationService) {
        DomainRegistry.bizCartValidationService = bizCartValidationService;
    }

    @Autowired
    private void setAddressValidationService(AddressValidationService addressValidationService) {
        DomainRegistry.addressValidationService = addressValidationService;
    }

    @Autowired
    private void setAddressRepository(AddressRepository addressRepository) {
        DomainRegistry.addressRepository = addressRepository;
    }

    @Autowired
    private void setBizOrderService(BizOrderService bizOrderService) {
        DomainRegistry.bizOrderService = bizOrderService;
    }

    @Autowired
    private void setBizOderSummaryRepository(BizOderSummaryRepository bizOrderRepository) {
        DomainRegistry.bizOderSummaryRepository = bizOrderRepository;
    }
}
