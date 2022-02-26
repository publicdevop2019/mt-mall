package com.mt.shop.application.biz_order.command;

import com.mt.shop.domain.model.biz_order.ShippingDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class CustomerPlaceBizOrderCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private BizOrderAddressCmdRep address;
    private List<BizOrderItemCommand> productList;
    private String paymentType;
    private BigDecimal paymentAmt;

    @Data
    public static class BizOrderItemCommand implements Serializable {
        private static final long serialVersionUID = 1;
        private String name;
        private String id;
        private List<BizOrderItemAddOnCommand> selectedOptions;
        private BigDecimal finalPrice;
        private String productId;
        private String skuId;
        private Integer amount;
        private Integer version;
        private String imageUrlSmall;
        private Set<String> attributesSales;
        private Map<String, String> attrIdMap;

    }

    @Data
    @NoArgsConstructor
    public static class BizOrderAddressCmdRep implements Serializable {
        private static final long serialVersionUID = 1;
        private String fullName;
        private String line1;
        private String line2;
        private String postalCode;
        private String phoneNumber;
        private String city;
        private String province;
        private String country;

        public BizOrderAddressCmdRep(CustomerUpdateBizOrderAddressCommand command) {
            fullName = command.getFullName();
            line1 = command.getLine1();
            line2 = command.getLine2();
            postalCode = command.getPostalCode();
            phoneNumber = command.getPhoneNumber();
            city = command.getCity();
            province = command.getProvince();
            country = command.getCountry();
        }

        public BizOrderAddressCmdRep(ShippingDetail address) {
            setCountry(address.getCountry());
            setProvince(address.getProvince());
            setCity(address.getCity());
            setPostalCode(address.getPostalCode());
            setLine1(address.getLine1());
            setLine2(address.getLine2());
            setPhoneNumber(address.getPhoneNumber());
            setFullName(address.getFullName());
        }
    }

    @Data
    @AllArgsConstructor
    public static class BizOrderItemAddOnSelectionCommand implements Serializable {
        private static final long serialVersionUID = 1;
        private String optionValue;

        private String priceVar;

    }

    @Data
    public static class BizOrderItemAddOnCommand implements Serializable {
        private static final long serialVersionUID = 1;
        private String title;

        private List<BizOrderItemAddOnSelectionCommand> options;

    }
}
