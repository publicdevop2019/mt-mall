package com.mt.payment.infrastructure;

import com.mt.common.domain.model.domain_event.MQHelper;

public class AppConstant {
    public static final String GENERATE_PAYMENT_QR_LINK_EVENT = "generate_payment_qr_link_event";
    public static final String REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT = "remove_payment_qr_link_for_invalid_event";
}
