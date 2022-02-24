package com.mt.shop.application.address.representation;

import com.mt.shop.domain.model.address.Address;
import lombok.Data;

@Data
public class AddressCardRepresentation {
    private String id;

    private String fullName;

    private String postalCode;

    private String phoneNumber;

    private String city;

    private String province;

    private String country;
    private long createdAt;

    public AddressCardRepresentation(Address address) {
        this.id = address.getAddressId().getDomainId();
        this.fullName = address.getFullName();
        this.postalCode = address.getPostalCode();
        this.phoneNumber = address.getPhoneNumber();
        this.city = address.getCity();
        this.province = address.getProvince();
        this.country = address.getCountry();
        this.createdAt = address.getCreatedAt().getTime();
    }

}
