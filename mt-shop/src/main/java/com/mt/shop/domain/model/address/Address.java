package com.mt.shop.domain.model.address;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.shop.application.address.command.CustomerCreateAddressCommand;
import com.mt.shop.application.address.command.CustomerUpdateAddressCommand;
import com.mt.shop.domain.DomainRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "address_")
@Getter
@NoArgsConstructor
public class Address extends Auditable {
    @Id
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Embedded
    private AddressId addressId;

    @Column(nullable = false)
    private String line1;

    private String line2;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String country;

    private void setFullName(String fullName) {
        Validator.notBlank(fullName);
        this.fullName = fullName;
    }

    private void setLine1(String line1) {
        Validator.notBlank(fullName);
        this.line1 = line1;
    }

    private void setLine2(String line2) {
        this.line2 = line2;
    }

    private void setPostalCode(String postalCode) {
        Validator.notBlank(fullName);
        this.postalCode = postalCode;
    }

    private void setPhoneNumber(String phoneNumber) {
        Validator.notBlank(fullName);
        this.phoneNumber = phoneNumber;
    }

    private void setCity(String city) {
        Validator.notBlank(fullName);
        this.city = city;
    }

    private void setProvince(String province) {
        Validator.notBlank(fullName);
        this.province = province;
    }

    private void setCountry(String country) {
        Validator.notBlank(fullName);
        this.country = country;
    }

    public static Address create(AddressId addressId, CustomerCreateAddressCommand command) {
        Address address = new Address(addressId, command);
        DomainRegistry.getAddressValidationService().validate(address,new HttpValidationNotificationHandler());
        return address;
    }

    public void replace(CustomerUpdateAddressCommand command) {
        setFullName(command.getFullName());
        setLine1(command.getLine1());
        setLine2(command.getLine2());
        setPostalCode(command.getPostalCode());
        setPhoneNumber(command.getPhoneNumber());
        setCity(command.getCity());
        setProvince(command.getProvince());
        setCountry(command.getCountry());
        validate(new HttpValidationNotificationHandler());
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        AddressValidator addressValidator = new AddressValidator(this, handler);
        addressValidator.validate();
    }

    private Address(AddressId id, CustomerCreateAddressCommand command) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.addressId = id;
        setFullName(command.getFullName());
        setLine1(command.getLine1());
        setLine2(command.getLine2());
        setPostalCode(command.getPostalCode());
        setPhoneNumber(command.getPhoneNumber());
        setCity(command.getCity());
        setProvince(command.getProvince());
        setCountry(command.getCountry());
        validate(new HttpValidationNotificationHandler());
    }

    public static boolean isDuplicateOf(Address a, Address b) {
        return Objects.equals(b.getFullName(), a.getFullName()) &&
                Objects.equals(b.getLine1(), a.getLine1()) &&
                Objects.equals(b.getLine2(), a.getLine2()) &&
                Objects.equals(b.getPostalCode(), a.getPostalCode()) &&
                Objects.equals(b.getPhoneNumber(), a.getPhoneNumber()) &&
                Objects.equals(b.getCity(), a.getCity()) &&
                Objects.equals(b.getProvince(), a.getProvince()) &&
                Objects.equals(b.getCountry(), a.getCountry());
    }
}
