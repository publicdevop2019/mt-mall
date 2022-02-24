package com.mt.shop.port.adapter.persistence.biz_order;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.shop.domain.model.biz_order.CartDetail;

import javax.persistence.AttributeConverter;
import java.util.List;

public class BizOrderItemConverter implements AttributeConverter<List<CartDetail>, String> {
    @Override
    public String convertToDatabaseColumn(List<CartDetail> attribute) {
        return CommonDomainRegistry.getCustomObjectSerializer().serializeCollection(attribute);
    }

    @Override
    public List<CartDetail> convertToEntityAttribute(String dbData) {
        return List.copyOf(CommonDomainRegistry.getCustomObjectSerializer().deserializeCollection(dbData, CartDetail.class));
    }
}
