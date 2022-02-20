package com.mt.user_profile.application.biz_order.command;

import com.mt.user_profile.domain.biz_order.BizOrder;
import com.mt.user_profile.domain.biz_order.BizOrderId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
public class CustomerUpdateBizOrderAddressCommand implements Serializable{
    private static final long serialVersionUID = 1;
    @TargetAggregateIdentifier
    private BizOrderId bizOrderId;
    private String fullName;

    private String line1;

    private String line2;

    private String postalCode;

    private String phoneNumber;

    private String city;

    private String province;

    private String country;
    private String changeId;
    private Integer version;
    private Date modifiedByUserAt;
    public CustomerUpdateBizOrderAddressCommand(UpdateOrderForUpdateAddressCommand command) {
        setBizOrderId(new BizOrderId(command.getOrderId()));
        CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep address = command.getAddress();
        setFullName(address.getFullName());
        setLine1(address.getLine1());
        setLine2(address.getLine2());
        setPostalCode(address.getPostalCode());
        setPhoneNumber(address.getPhoneNumber());
        setCity(address.getCity());
        setProvince(address.getProvince());
        setCountry(address.getCountry());
        setChangeId(command.getChangeId());
        setVersion(command.getOrderVersion());
        setModifiedByUserAt(Date.from(Instant.now()));
    }

    public CustomerUpdateBizOrderAddressCommand(CancelUpdateOrderForUpdateOrderAddressCommand command) {
        setBizOrderId(new BizOrderId(command.getOrderId()));
        CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep address = command.getAddress();
        setFullName(address.getFullName());
        setLine1(address.getLine1());
        setLine2(address.getLine2());
        setPostalCode(address.getPostalCode());
        setPhoneNumber(address.getPhoneNumber());
        setCity(address.getCity());
        setProvince(address.getProvince());
        setCountry(address.getCountry());
        setChangeId(command.getChangeId());
        setVersion(command.getOrderVersion()+1);
        setModifiedByUserAt(new Date(command.getModifiedByUserAt()));
    }
}
