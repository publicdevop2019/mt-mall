package com.mt.shop.domain.model.catalog;

import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.tag.Tag;
import com.mt.shop.domain.model.tag.TagQuery;
import com.mt.shop.domain.model.tag.TagValueType;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogValidationService {
    public void validate(Set<LinkedTag> linkedTags, ValidationNotificationHandler handler) {
        Set<Tag> tagSet = QueryUtility.getAllByQuery((query) -> DomainRegistry.getTagRepository().tagsOfQuery((TagQuery) query), new TagQuery(linkedTags.stream().map(LinkedTag::getTagId).collect(Collectors.toSet())));
        linkedTags.forEach(linkedTag -> {
            Optional<Tag> first = tagSet.stream().filter(e -> e.getTagId().equals(linkedTag.getTagId())).findFirst();
            if (first.isEmpty()) {
                handler.handleError("specified tag not found: " + linkedTag.getTagId().getDomainId());
            }
            Tag tag = first.get();
            if (!TagValueType.MANUAL.equals(tag.getMethod())) {
                if (!tag.getSelectValues().contains(linkedTag.getTagValue()))
                    handler.handleError("specified tag value not found: " + linkedTag.getTagId().getDomainId() + " value: " + linkedTag.getTagValue());
            }
        });
    }
}
