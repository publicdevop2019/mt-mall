package com.mt.shop.port.adapter.persistence.cart;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.shop.domain.model.biz_order.BizOrderItemAddOn;

import javax.persistence.AttributeConverter;
import java.util.Collection;
import java.util.List;

public class BizOrderItemAddOnConverter implements AttributeConverter<List<BizOrderItemAddOn>, String> {

    @Override
    public String convertToDatabaseColumn(List<BizOrderItemAddOn> attribute) {
        return CommonDomainRegistry.getCustomObjectSerializer().serializeCollection(attribute);
    }

    @Override
    public List<BizOrderItemAddOn> convertToEntityAttribute(String dbData) {
        Collection<BizOrderItemAddOn> bizOrderItemAddOns = CommonDomainRegistry.getCustomObjectSerializer().deserializeCollection(dbData, BizOrderItemAddOn.class);
        return List.copyOf(bizOrderItemAddOns);
    }
}
