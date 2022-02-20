package com.mt.shop.domain.model.product;

import com.mt.common.CommonConstant;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.tag.Tag;
import com.mt.shop.domain.model.tag.TagId;
import com.mt.shop.domain.model.tag.TagQuery;
import com.mt.shop.domain.model.tag.Type;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductValidationService {
    public void validate(List<ProductAttrSaleImages> saleImages, ValidationNotificationHandler handler) {
        if (saleImages != null && !saleImages.isEmpty()) {
            Set<TagId> collect = saleImages.stream().map(e -> e.getAttributeSales().split(CommonConstant.QUERY_DELIMITER)[0]).map(TagId::new).collect(Collectors.toSet());
            Set<Tag> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getTagRepository().tagsOfQuery((TagQuery) query), new TagQuery(collect));
            if (allByQuery.size() != collect.size())
                handler.handleError("unable find all sales tags");
            if (allByQuery.stream().anyMatch(e -> !e.getType().equals(Type.SALES_ATTR)))
                handler.handleError("should not have non sales tags");
        }
    }
}
