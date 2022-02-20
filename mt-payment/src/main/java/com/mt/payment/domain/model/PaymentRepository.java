package com.mt.payment.domain.model;


import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> searchPaymentByOrderId(String orderId);
    Payment createOrUpdate(Payment payment);
}
