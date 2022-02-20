package com.mt.shop.domain.model.product;

import com.mt.shop.domain.model.tag.TagId;

import java.util.Optional;

public interface ProductTagRepository {
    Optional<ProductTag> findByTagIdAndTagValueAndType(TagId tagId, String value, TagType tagTypeEnum);
}
