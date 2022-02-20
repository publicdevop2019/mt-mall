package com.mt.shop.application.product.representation;

import com.mt.shop.domain.model.product.Product;
import lombok.Data;

@Data
public
class ProductCardRepresentation {
    private String id;
    private String name;
    private String coverImage;
    private Integer totalSales;
    private Long startAt;
    private Long endAt;
    private Integer version;
    private boolean reviewRequired = false;

    public ProductCardRepresentation(Product product, boolean review) {
        setId(product.getProductId().getDomainId());
        setName(product.getName());
        setCoverImage(product.getImageUrlSmall());
        setTotalSales(product.getTotalSales());
        setStartAt(product.getStartAt());
        setEndAt(product.getEndAt());
        setVersion(product.getVersion());
        setReviewRequired(review);
    }

}
