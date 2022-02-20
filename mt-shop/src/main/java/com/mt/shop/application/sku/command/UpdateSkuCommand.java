package com.mt.shop.application.sku.command;

import com.mt.shop.domain.model.sku.SkuId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class UpdateSkuCommand implements Serializable{
    private static final long serialVersionUID = 1;
    private Integer version;
    private BigDecimal price;
    private String description;
    private SkuId skuId;
}
