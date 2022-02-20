package com.mt.shop.application.product.representation;

import com.mt.shop.domain.model.product.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PublicProductCardRepresentation {
    private String id;
    private String name;
    private String imageUrlSmall;
    private String description;
    private BigDecimal lowestPrice;
    private Integer totalSales;

    public PublicProductCardRepresentation(Object obj) {
        Product product = (Product) obj;
        setId(product.getProductId().getDomainId());
        setName(product.getName());
        setImageUrlSmall(product.getImageUrlSmall());
        setDescription(product.getDescription());
        setTotalSales(product.getTotalSales());
        setLowestPrice(product.getLowestPrice());
    }


}
