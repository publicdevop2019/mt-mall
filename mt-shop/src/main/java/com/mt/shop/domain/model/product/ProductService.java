package com.mt.shop.domain.model.product;

import com.mt.shop.application.product.command.CreateProductCommand;
import com.mt.shop.application.product.command.ProductOptionCommand;
import com.mt.shop.application.product.command.ProductSalesImageCommand;
import com.mt.shop.domain.DomainRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    public ProductId create(ProductId productId,
                            String name,
                            String imageUrlSmall,
                            Set<String> imageUrlLarge,
                            String description,
                            Long startAt,
                            Long endAt,
                            List<ProductOptionCommand> selectedOptions,
                            Set<String> attributesKey,
                            Set<String> attributesProd,
                            Set<String> attributesGen,
                            List<CreateProductCommand.CreateProductSkuAdminCommand> skus,
                            List<ProductSalesImageCommand> attributeSaleImages
                            ) {
        Product product = new Product(productId, name, imageUrlSmall, imageUrlLarge,
                description, startAt, endAt, selectedOptions, attributesKey, attributesProd, attributesGen, skus, attributeSaleImages);
        DomainRegistry.getProductRepository().add(product);
        return product.getProductId();
    }
}
