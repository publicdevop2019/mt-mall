package com.mt.shop.domain.model.product.event;

import com.mt.shop.domain.model.product.ProductId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductUpdated extends ProductEvent{
    public ProductUpdated(ProductId productId) {
        super(productId);
    }
}
