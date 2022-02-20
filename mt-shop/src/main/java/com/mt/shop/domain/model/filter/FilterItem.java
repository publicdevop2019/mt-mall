package com.mt.shop.domain.model.filter;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import com.mt.shop.domain.model.tag.TagId;
import lombok.*;

import javax.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.Set;

@Getter
@NoArgsConstructor
public class FilterItem implements Serializable {
    private static final long serialVersionUID = 1;
    @Setter(AccessLevel.PRIVATE)
    private TagId tagId;
    private String name;
    private Set<String> values;

    public FilterItem(TagId id, String name, Set<String> selectValues) {
        setTagId(id);
        setName(name);
        setValues(selectValues);
    }

    public void setName(String name) {
        Validator.whitelistOnly(name);
        this.name = name;
    }

    public void setValues(Set<String> values) {
        Validator.notEmpty(values);
        this.values = values;
    }

    public static class FilterItemConverter implements AttributeConverter<Set<FilterItem>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<FilterItem> redirectURLS) {
            return CommonDomainRegistry.getCustomObjectSerializer().nativeSerialize(redirectURLS);
        }

        @Override
        public Set<FilterItem> convertToEntityAttribute(byte[] bytes) {
            return (Set<FilterItem>) CommonDomainRegistry.getCustomObjectSerializer().nativeDeserialize(bytes);
        }
    }
}
