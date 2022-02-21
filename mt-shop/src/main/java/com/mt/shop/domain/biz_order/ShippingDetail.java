package com.mt.shop.domain.biz_order;

import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.shop.application.biz_order.command.CustomerPlaceBizOrderCommand;
import com.mt.shop.domain.biz_order.event.UpdateBizOrderAddressEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ShippingDetail implements Serializable {
    private static final long serialVersionUID = 1;
    @Column
    private String fullName;

    @Column
    private String line1;

    @Column
    private String line2;

    @Column
    private String postalCode;

    @Column
    private String phoneNumber;

    @Column
    private String city;

    @Column
    private String province;

    @Column
    private String country;

    public ShippingDetail(CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep address) {
        setFullName(address.getFullName());
        setLine1(address.getLine1());
        setLine2(address.getLine2());
        setPostalCode(address.getPostalCode());
        setPhoneNumber(address.getPhoneNumber());
        setCity(address.getCity());
        setProvince(address.getProvince());
        setCountry(address.getCountry());
        ShippingDetailValidator shippingDetailValidator = new ShippingDetailValidator(this, new HttpValidationNotificationHandler());
        shippingDetailValidator.validate();
    }

    public ShippingDetail(UpdateBizOrderAddressEvent event) {
        setFullName(event.getFullName());
        setLine1(event.getLine1());
        setLine2(event.getLine2());
        setPostalCode(event.getPostalCode());
        setPhoneNumber(event.getPhoneNumber());
        setCity(event.getCity());
        setProvince(event.getProvince());
        setCountry(event.getCountry());
        ShippingDetailValidator shippingDetailValidator = new ShippingDetailValidator(this, new HttpValidationNotificationHandler());
        shippingDetailValidator.validate();
    }

    private void setFullName(String fullName) {
        this.fullName = fullName;
    }

    private void setLine1(String line1) {
        this.line1 = line1;
    }

    private void setLine2(String line2) {
        this.line2 = line2;
    }

    private void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void setCity(String city) {
        this.city = city;
    }

    private void setProvince(String province) {
        this.province = province;
    }

    private void setCountry(String country) {
        this.country = country;
    }
}
