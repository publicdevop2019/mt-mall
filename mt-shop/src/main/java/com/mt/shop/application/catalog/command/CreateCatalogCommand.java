package com.mt.shop.application.catalog.command;

import com.mt.shop.domain.model.catalog.Type;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class CreateCatalogCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private String parentId;
    private Set<String> attributes;
    private Type catalogType;
}
