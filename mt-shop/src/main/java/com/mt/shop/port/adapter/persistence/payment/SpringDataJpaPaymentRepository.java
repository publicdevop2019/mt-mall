package com.mt.shop.port.adapter.persistence.payment;


import com.mt.shop.domain.model.payment.Payment;
import com.mt.shop.domain.model.payment.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SpringDataJpaPaymentRepository extends JpaRepository<Payment, Long>, PaymentRepository {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.orderId = ?1")
    Optional<Payment> getPaymentByOrderId(String orderId);

    default Optional<Payment> searchPaymentByOrderId(String orderId) {
        return getPaymentByOrderId(orderId);
    }

    default Payment createOrUpdate(Payment payment) {
        return save(payment);
    }
}
