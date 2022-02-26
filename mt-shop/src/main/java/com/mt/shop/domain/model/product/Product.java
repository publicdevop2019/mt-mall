package com.mt.shop.domain.model.product;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.exception.AggregateNotExistException;
import com.mt.common.domain.model.restful.exception.NoUpdatableFieldException;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.product.command.CreateProductCommand;
import com.mt.shop.application.product.command.ProductOptionCommand;
import com.mt.shop.application.product.command.ProductSalesImageCommand;
import com.mt.shop.application.product.command.UpdateProductCommand;
import com.mt.shop.application.product.exception.NoLowestPriceFoundException;
import com.mt.shop.application.product.exception.SkuAlreadyExistException;
import com.mt.shop.application.product.exception.SkuNotExistException;
import com.mt.shop.application.sku.command.CreateSkuCommand;
import com.mt.shop.application.sku.command.UpdateSkuCommand;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.product.event.ProductCreated;
import com.mt.shop.domain.model.product.event.ProductSkuUpdated;
import com.mt.shop.domain.model.product.event.ProductUpdated;
import com.mt.shop.domain.model.sku.Sku;
import com.mt.shop.domain.model.sku.SkuId;
import com.mt.shop.domain.model.tag.TagId;
import com.mt.shop.application.product.representation.ProductRepresentation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.*;


@Getter
@Slf4j
@Entity
@Table(name = "product_")
@NoArgsConstructor
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    private String imageUrlSmall;
    private String name;
    private String description;
    private Long endAt;
    private Long startAt;

    @Column(length = 10000)
    @Convert(converter = ProductOption.ProductOptionConverter.class)
    private List<ProductOption> selectedOptions;

    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "productId", unique = true, updatable = false, nullable = false))
    })
    private ProductId productId;
    @Column(length = 10000)
    @Setter(AccessLevel.PRIVATE)
    private HashMap<String, String> attrSalesMap;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "product_tag_map",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<ProductTag> tags = new HashSet<>();

    private BigDecimal lowestPrice;

    @Column
    private Integer totalSales;

    public Product(ProductId productId,
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
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setImageUrlSmall(imageUrlSmall);
        setName(name);
        setProductId(productId);
        setDescription(description);
        setSelectedOptions(selectedOptions);
        setImageUrlLarge(imageUrlLarge);
        if (attributesProd != null)
            attributesProd.forEach(getStringConsumer(TagType.PROD));
        if (attributesKey != null)
            attributesKey.forEach(getStringConsumer(TagType.KEY));
        if (attributesGen != null)
            attributesGen.forEach(getStringConsumer(TagType.GEN));
        setStartAt(startAt);
        setEndAt(endAt);
        skus.forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(e.getAttributesSales());
        });
        skus.stream().map(CreateProductCommand.CreateProductSkuAdminCommand::getAttributesSales)
                .flatMap(Collection::stream).collect(Collectors.toSet())
                .forEach(getStringConsumer(TagType.SALES));
        List<CreateSkuCommand> createSkuCommands = new ArrayList<>();
        for (CreateProductCommand.CreateProductSkuAdminCommand skuAdminCommand : skus) {
            CreateSkuCommand command1 = new CreateSkuCommand();
            command1.setPrice(skuAdminCommand.getPrice());
            command1.setReferenceId(productId);
            command1.setStorageOrder(skuAdminCommand.getStorageOrder());
            command1.setStorageActual(skuAdminCommand.getStorageActual());
            command1.setSales(skuAdminCommand.getSales());
            SkuId skuId = new SkuId();
            command1.setSkuId(skuId);
            createSkuCommands.add(command1);
            getAttrSalesMap().put(String.join(",", skuAdminCommand.getAttributesSales()), skuId.getDomainId());
        }
        setAttributeSaleImages(attributeSaleImages);
        setLowestPrice(findLowestPrice(skus));
        setTotalSales(calcTotalSales(skus));
        DomainEventPublisher.instance().publish(new ProductCreated(productId, createSkuCommands, UUID.randomUUID().toString()));
    }


    private void setLowestPrice(BigDecimal lowestPrice) {
        Validator.greaterThanOrEqualTo(lowestPrice, BigDecimal.ZERO);
        lowestPrice = lowestPrice.setScale(2, RoundingMode.CEILING);
        if (this.lowestPrice == null) {
            this.lowestPrice = lowestPrice;
            return;
        }
        BigDecimal bigDecimal = this.lowestPrice.setScale(2, RoundingMode.CEILING);
        if (bigDecimal.compareTo(lowestPrice) != 0)
            this.lowestPrice = lowestPrice;
    }

    private void setTotalSales(Integer totalSales) {
        Validator.greaterThanOrEqualTo(totalSales, 0);
        this.totalSales = totalSales;
    }

    private void setEndAt(Long endAt) {
        this.endAt = endAt;
    }

    private void setSelectedOptions(List<ProductOptionCommand> selectedOptions) {
        if (selectedOptions != null)
            this.selectedOptions = selectedOptions.stream().map(ProductOption::new).collect(Collectors.toList());
    }

    private void setStartAt(Long startAt) {
        this.startAt = startAt;
    }

    private void setImageUrlSmall(String imageUrlSmall) {
        Validator.isHttpUrl(imageUrlSmall);
        this.imageUrlSmall = imageUrlSmall;
    }

    private void setDescription(String description) {
        Validator.lengthLessThanOrEqualTo(description, 50);
        Validator.whitelistOnly(description);
        this.description = description;
    }

    private void setName(String name) {
        Validator.notBlank(name);
        Validator.lengthLessThanOrEqualTo(name, 75);
        Validator.whitelistOnly(name);
        this.name = name;
    }

    private void setImageUrlLarge(@Nullable Set<String> imageUrlLarge) {
        if (imageUrlLarge != null)
            imageUrlLarge.forEach(Validator::isHttpUrl);
        if (imageUrlLarge == null && this.imageUrlLarge != null && this.imageUrlLarge.isEmpty())
            return;
        this.imageUrlLarge = imageUrlLarge;
    }

    private void setAttributeSaleImages(ArrayList<ProductAttrSaleImages> attributeSaleImages) {
        DomainRegistry.getProductValidationService().validate(attributeSaleImages, new HttpValidationNotificationHandler());
        this.attributeSaleImages = attributeSaleImages;
    }

    private void addTag(ProductTag tag) {
        tags.add(tag);
        tag.getProducts().add(this);
    }

    private void removeTag(ProductTag tag) {
        tags.remove(tag);
        tag.getProducts().remove(this);
    }

    public void replace(String name,
                        String imageUrlSmall,
                        Set<String> imageUrlLarge,
                        String description,
                        Long startAt,
                        Long endAt,
                        List<ProductOptionCommand> selectedOptions,
                        Set<String> attributesKey,
                        Set<String> attributesProd,
                        Set<String> attributesGen,
                        List<UpdateProductCommand.UpdateProductAdminSkuCommand> skus,
                        List<ProductSalesImageCommand> attributeSaleImages,
                        String changeId
    ) {
        setImageUrlSmall(imageUrlSmall);
        setName(name);
        setDescription(description);
        setSelectedOptions(selectedOptions);
        setImageUrlLarge(imageUrlLarge);
        setStartAt(startAt);
        setEndAt(endAt);
        Validator.notEmpty(skus);
        skus.forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            Validator.notNull(e.getAttributesSales());
        });
        adjustSku(skus, changeId);
        updateAttributeSaleImages(attributeSaleImages);
        setLowestPrice(findLowestPrice2(skus));
        Set<String> sales = skus.stream().map(UpdateProductCommand.UpdateProductAdminSkuCommand::getAttributesSales)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        if (!getTags().equals(getProductTags(sales, attributesKey, attributesGen, attributesProd))) {
            this.tags = new HashSet<>();
            sales.forEach(getStringConsumer(TagType.SALES));
            if (attributesProd != null)
                attributesProd.forEach(getStringConsumer(TagType.PROD));
            if (attributesKey != null) {
                attributesKey.forEach(getStringConsumer(TagType.KEY));
            }
            if (attributesGen != null)
                attributesGen.forEach(getStringConsumer(TagType.GEN));
        }
        DomainEventPublisher.instance().publish(new ProductUpdated(productId));
    }

    private Set<ProductTag> getProductTags(Set<String> sales, Set<String> attributesKey, Set<String> attributesGen, Set<String> attributesProd) {
        Set<ProductTag> productTags = new HashSet<>();
        if (sales != null) {
            sales.forEach(e -> {
                String[] split = e.split(":");
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagIdAndTagValueAndType(new TagId(split[0]), split[1], TagType.SALES);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.SALES);
                    productTags.add(tag);
                }
            });
        }
        if (attributesKey != null) {
            attributesKey.forEach(e -> {
                String[] split = e.split(":");
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagIdAndTagValueAndType(new TagId(split[0]), split[1], TagType.KEY);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.KEY);
                    productTags.add(tag);
                }
            });
        }
        if (attributesGen != null) {
            attributesGen.forEach(e -> {
                String[] split = e.split(":");
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagIdAndTagValueAndType(new TagId(split[0]), split[1], TagType.GEN);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.GEN);
                    productTags.add(tag);
                }
            });
        }
        if (attributesProd != null) {
            attributesProd.forEach(e -> {
                String[] split = e.split(":");
                Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagIdAndTagValueAndType(new TagId(split[0]), split[1], TagType.PROD);
                if (byValue.isPresent()) {
                    productTags.add(byValue.get());
                } else {
                    ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, TagType.PROD);
                    productTags.add(tag);
                }
            });
        }
        return productTags;
    }

    private void updateAttributeSaleImages(List<ProductSalesImageCommand> attributeSaleImages) {
        if (attributeSaleImages != null) {
            ArrayList<ProductAttrSaleImages> collect = attributeSaleImages.stream().map(e ->
                    new ProductAttrSaleImages(e.getAttributeSales(), (LinkedHashSet<String>) e.getImageUrls())
            ).collect(Collectors.toCollection(ArrayList::new));
            setAttributeSaleImages(collect);
        }
    }

    private Consumer<String> getStringConsumer(TagType key) {
        return e -> {
            String[] split = e.split(":");
            Optional<ProductTag> byValue = DomainRegistry.getProductTagRepository().findByTagIdAndTagValueAndType(new TagId(split[0]), split[1], key);
            if (byValue.isPresent()) {
                addTag(byValue.get());
            } else {
                ProductTag tag = new ProductTag(CommonDomainRegistry.getUniqueIdGeneratorService().id(), e, key);
                addTag(tag);
            }
        };
    }

    public HashMap<String, String> getAttrSalesMap() {
        if (attrSalesMap == null) {
            attrSalesMap = new HashMap<>();
        }
        return attrSalesMap;
    }

    private void adjustSku(List<UpdateProductCommand.UpdateProductAdminSkuCommand> commands, String changeId) {
        List<CreateSkuCommand> createSkuCommands = new ArrayList<>();
        List<UpdateSkuCommand> updateSkuCommands = new ArrayList<>();
        Set<SkuId> removeSkuCommands = new HashSet<>();
        commands.forEach(command -> {
            if (command.getStorageActual() != null && command.getStorageOrder() != null) {
                // new sku
                if (getAttrSalesMap().containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuAlreadyExistException();
                }

                CreateSkuCommand command1 = new CreateSkuCommand();
                SkuId skuId = new SkuId();
                command1.setSkuId(skuId);
                command1.setPrice(command.getPrice());
                command1.setReferenceId(productId);
                command1.setStorageOrder(command.getStorageOrder());
                command1.setStorageActual(command.getStorageActual());
                command1.setSales(command.getSales() == null ? 0 : command.getSales());
                createSkuCommands.add(command1);
                getAttrSalesMap().put(String.join(",", command.getAttributesSales()), skuId.getDomainId());
            } else {
                //existing sku
                if (!getAttrSalesMap().containsKey(getAttrSalesKey(command.getAttributesSales()))) {
                    throw new SkuNotExistException();
                }
                //update price
                String s = getAttrSalesMap().get(getAttrSalesKey(command.getAttributesSales()));
                Optional<Sku> sku = ApplicationServiceRegistry.getSkuApplicationService().sku(s);
                if (sku.isPresent() && sku.get().getPrice().compareTo(command.getPrice()) != 0) {
                    UpdateSkuCommand updateSkuCommand = new UpdateSkuCommand();
                    updateSkuCommand.setPrice(command.getPrice());
                    updateSkuCommand.setVersion(command.getVersion());
                    updateSkuCommand.setSkuId(sku.get().getSkuId());
                    //price will be update in a different changeId
                    updateSkuCommands.add(updateSkuCommand);

                }
                updateStorage(command, changeId);

            }
        });
        // find skus not in update command & remove
        List<String> collect = getAttrSalesMap().keySet().stream().filter(e -> commands.stream().noneMatch(command -> getAttrSalesKey(command.getAttributesSales()).equals(e))).collect(Collectors.toList());
        Set<String> collect1 = collect.stream().map(e -> getAttrSalesMap().get(e)).collect(Collectors.toSet());
        if (collect1.size() > 0) {
            removeSkuCommands.addAll(collect1.stream().map(SkuId::new).collect(Collectors.toSet()));
        }
        DomainEventPublisher.instance().publish(new ProductSkuUpdated(productId, createSkuCommands, updateSkuCommands, removeSkuCommands, UUID.randomUUID().toString()));
    }

    private String getAttrSalesKey(Set<String> attributesSales) {
        return String.join(",", attributesSales);
    }

    private void updateStorage(UpdateProductCommand.UpdateProductAdminSkuCommand command, String changeId) {
        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        if (command.getDecreaseOrderStorage() != null && !command.getDecreaseOrderStorage().equals(0)) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ProductRepresentation.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ORDER_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseOrderStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (command.getDecreaseActualStorage() != null && !command.getDecreaseActualStorage().equals(0)) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, ProductRepresentation.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseActualStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseOrderStorage() != null && !command.getIncreaseOrderStorage().equals(0)) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ProductRepresentation.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ORDER_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseOrderStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseActualStorage() != null && !command.getIncreaseActualStorage().equals(0)) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, ProductRepresentation.ProductSkuAdminRepresentation.ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseActualStorage());
            patchCommand.setExpect(1);
            patchCommands.add(patchCommand);
        }
        if (patchCommands.size() > 0)
            ApplicationServiceRegistry.getProductApplicationService().internalPatchBatch(patchCommands);
    }

    private String toSkuQueryPath(UpdateProductCommand.UpdateProductAdminSkuCommand command, String storageType) {
        String s = getAttrSalesMap().get(getAttrSalesKey(command.getAttributesSales()));
        return "/" + s + "/" + storageType;
    }

    private void setAttributeSaleImages(List<ProductSalesImageCommand> attributeSaleImages) {
        if (attributeSaleImages != null) {
            ArrayList<ProductAttrSaleImages> collect = attributeSaleImages.stream().map(e -> new ProductAttrSaleImages(e.getAttributeSales(), (LinkedHashSet<String>) e.getImageUrls())
            ).collect(Collectors.toCollection(ArrayList::new));
            setAttributeSaleImages(collect);
        }
    }

    private Integer calcTotalSales(List<CreateProductCommand.CreateProductSkuAdminCommand> skus) {
        return skus.stream().map(CreateProductCommand.CreateProductSkuAdminCommand::getSales).reduce(0, Integer::sum);
    }

    private BigDecimal findLowestPrice(List<CreateProductCommand.CreateProductSkuAdminCommand> skus) {
        CreateProductCommand.CreateProductSkuAdminCommand createProductSkuAdminCommand = skus.stream().min(Comparator.comparing(CreateProductCommand.CreateProductSkuAdminCommand::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return createProductSkuAdminCommand.getPrice();
    }

    private BigDecimal findLowestPrice2(List<UpdateProductCommand.UpdateProductAdminSkuCommand> skus) {
        UpdateProductCommand.UpdateProductAdminSkuCommand updateProductAdminSkuCommand = skus.stream().min(Comparator.comparing(UpdateProductCommand.UpdateProductAdminSkuCommand::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return updateProductAdminSkuCommand.getPrice();
    }

    public void replace(String name, Long startAt, Long endAt) {
        setName(name);
        setStartAt(startAt);
        setEndAt(endAt);
        DomainEventPublisher.instance().publish(new ProductUpdated(productId));
    }
}
