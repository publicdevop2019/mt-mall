package com.mt.shop.application.filter.representation;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.shop.domain.model.filter.Filter;
import com.mt.shop.domain.model.filter.FilterItem;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FilterRepresentation {
    private String id;
    private Set<String> catalogs;
    private List<FilterItemRepresentation> filters;
    private String description;
    private Integer version;

    public FilterRepresentation(Filter filter) {
        setId(filter.getFilterId().getDomainId());
        setDescription(filter.getDescription());
        setCatalogs(filter.getCatalogs().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        setVersion(filter.getVersion());
        this.filters = filter.getFilterItems().stream().map(FilterItemRepresentation::new).collect(Collectors.toList());
    }

    @Data
    private static class FilterItemRepresentation {
        private String id;
        private String name;
        private Set<String> values;

        public FilterItemRepresentation(FilterItem e) {
            setId(e.getTagId().getDomainId());
            setName(e.getName());
            setValues(e.getValues());
        }
    }
}
