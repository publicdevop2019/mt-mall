package com.mt.shop.infrastructure;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.shop.domain.model.tag.TagId;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class MetaChangeTagIdConverter implements AttributeConverter<Set<TagId>, String> {
    @Override
    public String convertToDatabaseColumn(Set<TagId> attribute) {
        return String.join(",", attribute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
    }

    @Override
    public Set<TagId> convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return Arrays.stream(dbData.split(",")).map(TagId::new).collect(Collectors.toSet());
    }
}
