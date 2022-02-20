package com.mt.shop.domain.model.catalog;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import com.mt.shop.domain.model.tag.TagId;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Getter
public class LinkedTag implements Serializable {
    private TagId tagId;
    private String tagValue;

    public static class LinkedTagConverter implements AttributeConverter<Set<LinkedTag>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<LinkedTag> nativeSerialize) {
            return CommonDomainRegistry.getCustomObjectSerializer().nativeSerialize(nativeSerialize);
        }

        @Override
        public Set<LinkedTag> convertToEntityAttribute(byte[] bytes) {
            if(bytes==null){
                return Collections.emptySet();
            }
            return (Set<LinkedTag>) CommonDomainRegistry.getCustomObjectSerializer().nativeDeserialize(bytes);
        }
    }

    public LinkedTag(String raw) {
        String[] split = raw.split(":");
        setTagId(split[0]);
        setTagValue(split[1]);
    }

    private void setTagId(String tagId) {
        Validator.notBlank(tagId);
        this.tagId = new TagId(tagId);
    }

    private void setTagValue(String tagValue) {
        Validator.notBlank(tagValue);
        this.tagValue = tagValue;
    }
}
