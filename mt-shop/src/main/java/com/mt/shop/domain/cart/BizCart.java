package com.mt.shop.domain.cart;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.shop.application.cart.command.UserCreateBizCartItemCommand;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.biz_order.BizOrderItemAddOn;
import com.mt.shop.port.adapter.persistence.cart.BizOrderItemAddOnConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor
public class BizCart extends Auditable {

    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 10000)
    @Convert(converter = BizOrderItemAddOnConverter.class)
    private List<BizOrderItemAddOn> selectedOptions;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    private String imageUrlSmall;

    @Column(nullable = false)
    private String productId;
    @Column(nullable = false)
    private String skuId;
    private Integer amount;
    private CartItemId cartItemId;

    @Convert(converter = StringSetConverter.class)
    private Set<String> attributesSales;

    private HashMap<String, String> attrIdMap;

    private BizCart(CartItemId id, UserCreateBizCartItemCommand command) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        setCartItemId(id);
        setName(command.getName());
        setSelectedOptions(command.getSelectedOptions());
        setFinalPrice(command.getFinalPrice());
        setImageUrlSmall(command.getImageUrlSmall());
        setProductId(command.getProductId());
        setSkuId(command.getSkuId());
        setAmount(command.getAmount());
        setAttributesSales(command.getAttributesSales());
        setAttrIdMap(command.getAttrIdMap());
    }

    public static BizCart create(CartItemId id, UserCreateBizCartItemCommand command) {
        BizCart bizCart = new BizCart(id, command);
        DomainRegistry.getBizCartValidationService().validate(bizCart, new HttpValidationNotificationHandler());
        return bizCart;
    }

    private void setName(String name) {
        Validator.notBlank(name);
        this.name = name;
    }

    private void setSelectedOptions(List<BizOrderItemAddOn> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    private void setFinalPrice(BigDecimal finalPrice) {
        Validator.greaterThanOrEqualTo(finalPrice, BigDecimal.ZERO);
        this.finalPrice = finalPrice;
    }

    private void setImageUrlSmall(String imageUrlSmall) {
        Validator.isHttpUrl(imageUrlSmall);
        this.imageUrlSmall = imageUrlSmall;
    }

    private void setProductId(String productId) {
        Validator.notBlank(productId);
        this.productId = productId;
    }

    private void setSkuId(String skuId) {
        Validator.notBlank(skuId);
        this.skuId = skuId;
    }

    private void setAmount(Integer amount) {
        Validator.greaterThanOrEqualTo(amount, 1);
        this.amount = amount;
    }

    private void setCartItemId(CartItemId cartItemId) {
        Validator.notNull(cartItemId);
        this.cartItemId = cartItemId;
    }

    private void setAttributesSales(Set<String> attributesSales) {
        this.attributesSales = attributesSales;
    }

    private void setAttrIdMap(Map<String, String> attrIdMap) {
        if (attrIdMap != null)
            this.attrIdMap = new HashMap<>(attrIdMap);
    }
}
