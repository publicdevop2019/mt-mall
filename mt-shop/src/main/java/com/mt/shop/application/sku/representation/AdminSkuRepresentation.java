package com.mt.shop.application.sku.representation;

import com.mt.shop.domain.model.sku.Sku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdminSkuRepresentation {
    private String id;

    private String referenceId;

    private Integer storageOrder;

    private Integer storageActual;

    private BigDecimal price;

    private Integer sales;

    private String createdBy;

    private Date createdAt;

    private String modifiedBy;

    private Date modifiedAt;

    private String description;
    private Integer version;

    public AdminSkuRepresentation(Sku sku) {
        setId(sku.getSkuId().getDomainId());
        setReferenceId(sku.getReferenceId());
        setStorageOrder(sku.getStorageOrder());
        setStorageActual(sku.getStorageActual());
        setPrice(sku.getPrice());
        setSales(sku.getSales());
        setCreatedBy(sku.getCreatedBy());
        setCreatedAt(sku.getCreatedAt());
        setModifiedAt(sku.getModifiedAt());
        setModifiedBy(sku.getModifiedBy());
        setDescription(sku.getDescription());
        setVersion(sku.getVersion());
    }
}
