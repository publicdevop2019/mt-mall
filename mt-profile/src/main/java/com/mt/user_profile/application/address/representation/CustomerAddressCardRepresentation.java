package com.mt.user_profile.application.address.representation;

import com.mt.user_profile.domain.address.Address;
import lombok.Data;

@Data
public class CustomerAddressCardRepresentation {
    private String id;

    private String fullName;

    private String line1;

    private String line2;

    private String postalCode;

    private String phoneNumber;

    private String city;

    private String province;

    private String country;

    public CustomerAddressCardRepresentation(Object o) {
        Address address = (Address) o;
        this.id = address.getAddressId().getDomainId();
        this.fullName = address.getFullName();
        this.line1 = address.getLine1();
        this.line2 = address.getLine2();
        this.postalCode = address.getPostalCode();
        this.phoneNumber = address.getPhoneNumber();
        this.city = address.getCity();
        this.province = address.getProvince();
        this.country = address.getCountry();
    }
}
