package com.mt.saga.domain.model.order_state_machine.order;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class CartDetail implements Serializable {

    private static final long serialVersionUID = 1;

    private String name;

    private List<BizOrderItemAddOn> selectedOptions;

    private BigDecimal finalPrice;

    private String productId;
    private String skuId;
    private Integer amount;
    private String cartId;
    private Set<String> attributesSales;

    private String imageUrlSmall;
    private Integer version;

    private Map<String, String> attrIdMap;
}
