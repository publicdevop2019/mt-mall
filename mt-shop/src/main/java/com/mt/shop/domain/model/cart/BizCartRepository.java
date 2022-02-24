package com.mt.shop.domain.model.cart;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Set;

public interface BizCartRepository {

    void add(BizCart bizCartItem);

    SumPagedRep<BizCart> cartItemOfQuery(CartQuery cartQuery);
    SumPagedRep<BizCart> removedCartItemOfQuery(CartQuery cartQuery);

    SumPagedRep<BizCart> internalCartItemOfQuery(CartQuery cartQuery);

    void remove(BizCart e);

    void removeAll(Set<BizCart> allByQuery);

    void restoreAll(Set<BizCart> allByQuery);
}
