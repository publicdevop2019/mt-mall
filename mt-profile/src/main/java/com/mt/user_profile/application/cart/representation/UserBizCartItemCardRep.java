package com.mt.user_profile.application.cart.representation;

import com.mt.user_profile.domain.biz_order.BizOrderItemAddOn;
import com.mt.user_profile.domain.cart.BizCart;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
public class UserBizCartItemCardRep {
    private String id;

    private String name;

    private List<BizOrderItemAddOn> selectedOptions;

    private String finalPrice;

    private String imageUrlSmall;

    private String productId;
    private String skuId;
    private Integer amount;
    private Integer version;

    private Set<String> attributesSales;
    private HashMap<String, String> attrIdMap;

    public UserBizCartItemCardRep(Object o) {
        BizCart o1 = (BizCart) o;
        id = o1.getCartItemId().getDomainId();
        name = o1.getName();
        selectedOptions = o1.getSelectedOptions();
        finalPrice = o1.getFinalPrice().toString();
        imageUrlSmall = o1.getImageUrlSmall();
        productId = o1.getProductId();
        attributesSales = o1.getAttributesSales();
        attrIdMap = o1.getAttrIdMap();
        skuId = o1.getSkuId();
        amount = o1.getAmount();
        version=o1.getVersion();
    }

}
