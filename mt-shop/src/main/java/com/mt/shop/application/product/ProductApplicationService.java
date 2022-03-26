package com.mt.shop.application.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.CommonDomainRegistry;


import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.product.command.CreateProductCommand;
import com.mt.shop.application.product.command.PatchProductCommand;
import com.mt.shop.application.product.command.UpdateProductCommand;
import com.mt.shop.application.product.representation.ProductCardRepresentation;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.meta.Meta;
import com.mt.shop.domain.model.meta.MetaQuery;
import com.mt.shop.domain.model.product.Product;
import com.mt.shop.domain.model.product.ProductId;
import com.mt.shop.domain.model.product.ProductQuery;
import com.mt.shop.domain.model.product.event.ProductDeleted;
import com.mt.shop.domain.model.product.event.ProductPatchBatched;
import com.mt.shop.domain.model.sku.SkuId;
import com.mt.shop.application.product.representation.ProductRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductApplicationService {

    
    @Transactional
    public String create(CreateProductCommand command, String operationId) {
        return ApplicationServiceRegistry.getIdempotentWrapper().idempotent(operationId,
                (change) -> {
                    ProductId productId = new ProductId();
                    DomainRegistry.getProductService().create(
                            productId,
                            command.getName(),
                            command.getImageUrlSmall(),
                            command.getImageUrlLarge(),
                            command.getDescription(),
                            command.getStartAt(),
                            command.getEndAt(),
                            command.getSelectedOptions(),
                            command.getAttributesKey(),
                            command.getAttributesProd(),
                            command.getAttributesGen(),
                            command.getSkus(),
                            command.getAttributeSaleImages()
                    );
                    change.setReturnValue(productId.getDomainId());
                    return productId.getDomainId();
                }, "Product"
        );
    }

    public SumPagedRep<Product> internalOnlyProducts(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getProductRepository().productsOfQuery(new ProductQuery(queryParam, pageParam, skipCount, false));
    }

    public SumPagedRep<ProductCardRepresentation> products(String queryParam, String pageParam, String skipCount) {
        SumPagedRep<Product> productSumPagedRep = DomainRegistry.getProductRepository().productsOfQuery(new ProductQuery(queryParam, pageParam, skipCount, false));
        Set<ProductId> collect = productSumPagedRep.getData().stream().map(Product::getProductId).collect(Collectors.toSet());
        Set<Meta> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getMetaRepository().metaOfQuery((MetaQuery) e), new MetaQuery(new HashSet<>(collect)));
        List<ProductCardRepresentation> collect1 = productSumPagedRep.getData().stream().map(e -> {
            boolean review = false;
            Optional<Meta> first = allByQuery.stream().filter(ee -> ee.getDomainId().getDomainId().equalsIgnoreCase(e.getProductId().getDomainId())).findFirst();
            if (first.isPresent() && first.get().getHasChangedTag()) {
                review = true;
            }
            return new ProductCardRepresentation(e, review);
        }).collect(Collectors.toList());
        return new SumPagedRep<>(collect1, productSumPagedRep.getTotalItemCount());
    }

    public Optional<Product> product(String id) {
        return DomainRegistry.getProductRepository().productOfId(new ProductId(id));
    }

    public SumPagedRep<Product> publicProducts(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getProductRepository().productsOfQuery(new ProductQuery(queryParam, pageParam, skipCount, true));
    }

    public Optional<Product> publicProduct(String id) {
        return DomainRegistry.getProductRepository().publicProductOfId(new ProductId(id));
    }

    
    @Transactional
    public void replace(String id, UpdateProductCommand command, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (change) -> {
            ProductId productId = new ProductId(id);
            Optional<Product> optionalProduct = DomainRegistry.getProductRepository().productOfId(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.checkVersion(command.getVersion());
                product.replace(
                        command.getName(),
                        command.getImageUrlSmall(),
                        command.getImageUrlLarge(),
                        command.getDescription(),
                        command.getStartAt(),
                        command.getEndAt(),
                        command.getSelectedOptions(),
                        command.getAttributesKey(),
                        command.getAttributesProd(),
                        command.getAttributesGen(),
                        command.getSkus(),
                        command.getAttributeSaleImages(),
                        command.getChangeId()
                );
                DomainRegistry.getProductRepository().add(product);
            }
            return null;
        }, "Product");
    }

    
    @Transactional
    public void removeById(String id, String changeId) {
        ProductId productId = new ProductId(id);
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<Product> optionalProduct = DomainRegistry.getProductRepository().productOfId(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                DomainRegistry.getProductRepository().remove(product);
                Set<SkuId> collect = product.getAttrSalesMap().values().stream().map(SkuId::new).collect(Collectors.toSet());
                CommonDomainRegistry.getDomainEventRepository().append(new ProductDeleted(productId, collect, UUID.randomUUID().toString()));
            }
            return null;
        }, "Product");
    }

    
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (change) -> {
            ProductId productId = new ProductId(id);
            Optional<Product> optionalCatalog = DomainRegistry.getProductRepository().productOfId(productId);
            if (optionalCatalog.isPresent()) {
                Product product = optionalCatalog.get();
                PatchProductCommand beforePatch = new PatchProductCommand(product);
                PatchProductCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchProductCommand.class);
                product.replace(
                        afterPatch.getName(),
                        afterPatch.getStartAt(),
                        afterPatch.getEndAt()
                );
                return null;
            }
            return null;
        }, "Product");
    }

    
    @Transactional
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (ignored) -> {
            internalPatchBatch(commands);
            return null;
        }, "Product");
    }

    public void internalPatchBatch(List<PatchCommand> commands) {
        List<PatchCommand> skuChange = commands.stream().filter(e ->
                e.getPath().contains("/"+ProductRepresentation.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL )
                        ||
                        e.getPath().contains("/"+ProductRepresentation.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ORDER_LITERAL )).collect(Collectors.toList());
        List<PatchCommand> productChange = commands.stream().filter(e -> e.getPath().contains("/" + ProductRepresentation.ADMIN_REP_START_AT_LITERAL)
                ||
                e.getPath().contains("/" + ProductRepresentation.ADMIN_REP_START_AT_LITERAL)
        ).collect(Collectors.toList());
        if (!productChange.isEmpty())
            DomainRegistry.getProductRepository().patchBatch(productChange);
        if (!skuChange.isEmpty()) {
            CommonDomainRegistry.getDomainEventRepository().append(new ProductPatchBatched(skuChange));
        }
    }

}
