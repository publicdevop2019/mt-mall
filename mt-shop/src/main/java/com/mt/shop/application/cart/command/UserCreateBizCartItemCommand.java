package com.mt.shop.application.cart.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mt.shop.domain.biz_order.BizOrderItemAddOn;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class UserCreateBizCartItemCommand implements Serializable {
    private static final long serialVersionUID = 1;

    private String name;

    private List<BizOrderItemAddOn> selectedOptions;

    private BigDecimal finalPrice;

    private String imageUrlSmall;

    private String productId;
    private String skuId;
    private Integer amount;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributesSales;

    private Map<String, String> attrIdMap;

}
