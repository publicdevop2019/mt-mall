package com.mt.user_profile.application.address.representation;

import com.mt.user_profile.domain.address.Address;
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

    public AddressCardRepresentation(Object o) {
        Address address = (Address) o;
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
