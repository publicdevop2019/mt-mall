package com.mt.shop.domain.model.payment;

public interface ThirdPartyPaymentService {
    Boolean checkPaymentStatus(String prepayId);
}
