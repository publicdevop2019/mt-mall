package com.mt.shop.application.sku.representation;

import com.mt.shop.domain.model.sku.Sku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuRepresentation {
    private BigDecimal price;
    private Integer version;
    public SkuRepresentation(Sku sku) {
        setPrice(sku.getPrice());
        setVersion(sku.getVersion());
    }
}
