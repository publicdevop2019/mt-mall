package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.model.validate.Validator;
import com.mt.shop.application.biz_order.command.CustomerPlaceBizOrderCommand;
import com.mt.shop.application.biz_order.command.InternalCreateNewOrderCommand;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class CartDetail implements Serializable {

    private static final long serialVersionUID = 1;

    @Column
    private String name;

    @Column(length = 10000)
    private List<BizOrderItemAddOn> selectedOptions;

    @Column
    private BigDecimal finalPrice;

    @Column
    private String productId;

    private String skuId;
    private String cartId;
    private Integer amount;
    private Integer version;
    private Set<String> attributesSales;
    private String imageUrlSmall;
    private HashMap<String, String> attrIdMap;

    public CartDetail(CustomerPlaceBizOrderCommand.BizOrderItemCommand cmd) {
        setFinalPrice(cmd.getFinalPrice());
        setCartId(cmd.getId());
        setSkuId(cmd.getSkuId());
        setProductId(cmd.getProductId());
        setName(cmd.getName());
        setAmount(cmd.getAmount());
        setImageUrlSmall(cmd.getImageUrlSmall());
        setAttributesSales(cmd.getAttributesSales());
        setVersion(cmd.getVersion());
        if (cmd.getAttrIdMap() != null)
            setAttrIdMap(new HashMap<>(cmd.getAttrIdMap()));
        List<BizOrderItemAddOn> collect1 = null;
        if (cmd.getSelectedOptions() != null) {
            collect1 = cmd.getSelectedOptions().stream().map(e2 -> {
                BizOrderItemAddOn customerOrderItemAddOn = new BizOrderItemAddOn();
                customerOrderItemAddOn.setTitle(e2.getTitle());
                List<BizOrderItemAddOnSelection> collect = e2.getOptions().stream()
                        .map(e3 -> new BizOrderItemAddOnSelection(e3.getOptionValue(), e3.getPriceVar())).collect(Collectors.toList());
                customerOrderItemAddOn.setOptions(collect);
                return customerOrderItemAddOn;
            }).collect(Collectors.toList());
        }
        setSelectedOptions(collect1);
    }

    public CartDetail(InternalCreateNewOrderCommand.CartDetailCommand cmd) {
        setFinalPrice(cmd.getFinalPrice());
        setCartId(cmd.getCartId());
        setSkuId(cmd.getSkuId());
        setProductId(cmd.getProductId());
        setName(cmd.getName());
        setAmount(cmd.getAmount());
        setImageUrlSmall(cmd.getImageUrlSmall());
        setAttributesSales(cmd.getAttributesSales());
        setVersion(cmd.getVersion());
        if (cmd.getAttrIdMap() != null)
            setAttrIdMap(new HashMap<>(cmd.getAttrIdMap()));
        List<BizOrderItemAddOn> collect1 = null;
        if (cmd.getSelectedOptions() != null) {
            collect1 = cmd.getSelectedOptions().stream().map(e2 -> {
                BizOrderItemAddOn customerOrderItemAddOn = new BizOrderItemAddOn();
                customerOrderItemAddOn.setTitle(e2.getTitle());
                List<BizOrderItemAddOnSelection> collect = e2.getOptions().stream()
                        .map(e3 -> new BizOrderItemAddOnSelection(e3.getOptionValue(), e3.getPriceVar())).collect(Collectors.toList());
                customerOrderItemAddOn.setOptions(collect);
                return customerOrderItemAddOn;
            }).collect(Collectors.toList());
        }
        setSelectedOptions(collect1);
    }

    private void setVersion(Integer version) {
        this.version = version;
    }

    private void setSelectedOptions(List<BizOrderItemAddOn> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    private void setAttributesSales(Set<String> attributesSales) {
        this.attributesSales = attributesSales;
    }

    private void setImageUrlSmall(String imageUrlSmall) {
        this.imageUrlSmall = imageUrlSmall;
    }

    private void setAttrIdMap(HashMap<String, String> attrIdMap) {
        this.attrIdMap = attrIdMap;
    }

    private void setName(String name) {
        Validator.notBlank(name);
        this.name = name;
    }

    private void setFinalPrice(BigDecimal finalPrice) {
        Validator.greaterThan(finalPrice, BigDecimal.ZERO);
        this.finalPrice = finalPrice;
    }

    private void setProductId(String productId) {
        Validator.notBlank(productId);
        this.productId = productId;
    }

    private void setAmount(Integer amount) {
        Validator.greaterThanOrEqualTo(amount, 1);
        this.amount = amount;
    }

    private void setSkuId(String skuId) {
        Validator.notBlank(skuId);
        this.skuId = skuId;
    }

    private void setCartId(String cartId) {
        Validator.notBlank(cartId);
        this.cartId = cartId;
    }
}
