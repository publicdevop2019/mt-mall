package com.mt.saga.domain.model.order_state_machine.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class BizOrderItemCommand {
    private String name;
    private List<BizOrderItemAddOnCommand> selectedOptions;
    private BigDecimal finalPrice;
    private Long productId;
    private String imageUrlSmall;
    private Set<String> attributesSales;
    private Map<String, String> attrIdMap;

}
