package com.mt.user_profile.application.biz_order.representation;

import com.mt.user_profile.domain.biz_order.BizOrder;
import com.mt.user_profile.domain.biz_order.CartDetail;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AppBizOrderRepresentation {
    private Set<CartDetail> readOnlyProductList;

    public AppBizOrderRepresentation(BizOrder bizOrder) {
        readOnlyProductList = bizOrder.getCartDetails();
    }
}
