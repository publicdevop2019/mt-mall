package com.mt.payment.application;

import com.mt.common.domain.model.idempotent.IdempotentService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Getter
    private static IdempotentService idempotentService;
    @Getter
    private static PaymentApplicationService paymentApplicationService;
    @Autowired
    private void setPaymentApplicationService(PaymentApplicationService paymentApplicationService) {
        ApplicationServiceRegistry.paymentApplicationService = paymentApplicationService;
    }
    @Autowired
    private void setIdempotentService(IdempotentService idempotentWrapper) {
        ApplicationServiceRegistry.idempotentService = idempotentWrapper;
    }
}
