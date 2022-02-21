package com.mt.shop.domain.biz_order.event;

import com.mt.shop.application.biz_order.command.CustomerUpdateBizOrderAddressCommand;
import com.mt.shop.domain.biz_order.BizOrder;
import com.mt.shop.domain.biz_order.BizOrderId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Date;

@Data
@Setter(AccessLevel.PRIVATE)
public class UpdateBizOrderAddressEvent {
    private static final long serialVersionUID = 1;
    private BizOrderId bizOrderId;
    private String fullName;

    private String line1;

    private String line2;

    private String postalCode;

    private String phoneNumber;

    private String city;

    private String province;

    private String country;
    private Integer version;
    private Date modifiedByUserAt;
    private String changeId;

    public UpdateBizOrderAddressEvent(CustomerUpdateBizOrderAddressCommand command, BizOrder bizOrder) {
        this.bizOrderId = bizOrder.getOrderId();
        fullName = command.getFullName();
        line1 = command.getLine1();
        line2 = command.getLine2();
        postalCode = command.getPostalCode();
        phoneNumber = command.getPhoneNumber();
        city = command.getCity();
        province = command.getProvince();
        country = command.getCountry();
        version = command.getVersion();
        modifiedByUserAt = command.getModifiedByUserAt();
    }
}
