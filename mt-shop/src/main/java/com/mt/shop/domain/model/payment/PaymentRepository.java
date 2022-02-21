package com.mt.shop.domain.model.payment;


import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> searchPaymentByOrderId(String orderId);
    Payment createOrUpdate(Payment payment);
}
