package com.mt.shop.infrastructure;

import com.mt.shop.domain.model.payment.ThirdPartyPaymentService;
import org.springframework.stereotype.Service;

@Service
public class WeChatPaymentService implements ThirdPartyPaymentService {
    @Override
    public Boolean checkPaymentStatus(String prepayId) {
        return true;
    }
}
