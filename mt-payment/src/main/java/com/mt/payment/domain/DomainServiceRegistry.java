package com.mt.payment.domain;

import com.mt.payment.domain.model.PaymentRepository;
import com.mt.payment.domain.model.ThirdPartyPaymentService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainServiceRegistry {
    @Getter
    private static PaymentRepository paymentRepository;
    @Getter
    private static ThirdPartyPaymentService weChatPayService;

    @Autowired
    private void setPaymentRepository(PaymentRepository paymentRepository) {
        DomainServiceRegistry.paymentRepository = paymentRepository;
    }

    @Autowired
    private void setWeChatPayService(ThirdPartyPaymentService weChatPayService) {
        DomainServiceRegistry.weChatPayService = weChatPayService;
    }
}
