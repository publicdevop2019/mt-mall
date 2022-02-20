package com.mt.payment.infrastructure;

import com.mt.payment.domain.model.ThirdPartyPaymentService;
import org.springframework.stereotype.Service;

@Service
public class WeChatPaymentService implements ThirdPartyPaymentService {
    @Override
    public Boolean checkPaymentStatus(String prepayId) {
        return true;
    }
}
