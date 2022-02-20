package com.mt.shop.domain.model.filter.event;

import com.mt.shop.domain.model.filter.FilterId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FilterUpdated extends FilterEvent{
    public FilterUpdated(FilterId filterId) {
        super(filterId);
    }
}
