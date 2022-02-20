package com.mt.saga.domain.model.order_state_machine;

public interface PaymentService {
    Boolean confirmPaymentStatus(String orderId);

}
