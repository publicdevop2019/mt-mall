package com.mt.shop.domain.model.biz_order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
public class PaymentDetail {
    private PaymentType paymentType;

    private String paymentLink;

    private BigDecimal paymentAmt;

    private Date paymentDate;
@Setter
    private PaymentStatus paid = PaymentStatus.UNPAID;

    public enum PaymentStatus {
        PAID,
        UNPAID;
    }

    public enum PaymentType {
        WECHAT_PAY,
        ALI_PAY;
    }

    public PaymentDetail(PaymentType paymentType, BigDecimal paymentAmt) {
        this.paymentType = paymentType;
        this.paymentAmt = paymentAmt;
    }

}
