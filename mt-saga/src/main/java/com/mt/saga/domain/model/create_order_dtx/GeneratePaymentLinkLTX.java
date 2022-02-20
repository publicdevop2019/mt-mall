package com.mt.saga.domain.model.create_order_dtx;

import com.mt.saga.domain.model.common.LTXStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;

@Setter
@Getter
public class GeneratePaymentLinkLTX {
    @Column(name = "generatePaymentLinkStatus")
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus status = LTXStatus.PENDING;
    @Column(name = "generatePaymentLinkResults")
    private String results;

    public GeneratePaymentLinkLTX() {
    }
}
