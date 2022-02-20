package com.mt.shop.application.tag.representation;

import com.mt.shop.domain.model.tag.Tag;
import com.mt.shop.domain.model.tag.TagValueType;
import com.mt.shop.domain.model.tag.Type;
import lombok.Data;

import java.util.Set;

@Data
public class TagRepresentation {
    private String id;
    private String name;
    private String description;
    private TagValueType method;
    private Set<String> selectValues;
    private Type type;
    private Integer version;

    public TagRepresentation(Tag tag) {
        setId(tag.getTagId().getDomainId());
        setName(tag.getName());
        setDescription(tag.getDescription());
        setSelectValues(tag.getSelectValues());
        setMethod(tag.getMethod());
        setType(tag.getType());
        setVersion(tag.getVersion());
    }
}
