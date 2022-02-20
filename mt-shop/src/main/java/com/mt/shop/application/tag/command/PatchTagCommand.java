package com.mt.shop.application.tag.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mt.common.domain.model.restful.TypedClass;
import com.mt.shop.domain.model.tag.Tag;
import com.mt.shop.domain.model.tag.TagValueType;
import com.mt.shop.domain.model.tag.Type;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class PatchTagCommand extends TypedClass<PatchTagCommand> {

    private String name;

    private String description;

    private TagValueType method;
    @JsonDeserialize(as= LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> selectValues;

    private Type type;

    public PatchTagCommand(Tag bizAttribute) {
        super(PatchTagCommand.class);
        this.name = bizAttribute.getName();
        this.description = bizAttribute.getDescription();
        this.method = bizAttribute.getMethod();
        this.selectValues = bizAttribute.getSelectValues();
        this.type = bizAttribute.getType();
    }

    public PatchTagCommand() {
        super(PatchTagCommand.class);
    }
}
