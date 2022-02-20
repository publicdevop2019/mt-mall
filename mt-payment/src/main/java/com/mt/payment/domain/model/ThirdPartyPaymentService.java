package com.mt.payment.domain.model;

public interface ThirdPartyPaymentService {
    Boolean checkPaymentStatus(String prepayId);
}
