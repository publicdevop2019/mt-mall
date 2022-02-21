package com.mt.shop.application.biz_order.representation;

import com.mt.shop.domain.biz_order.BizOrder;
import com.mt.shop.domain.biz_order.CartDetail;
import lombok.Data;

import java.util.Set;

@Data
public class AppBizOrderRepresentation {
    private Set<CartDetail> readOnlyProductList;

    public AppBizOrderRepresentation(BizOrder bizOrder) {
        readOnlyProductList = bizOrder.getCartDetails();
    }
}
