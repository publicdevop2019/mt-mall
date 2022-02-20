package com.mt.shop.domain.model.tag.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.shop.domain.model.tag.TagId;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.shop.domain.model.tag.event.TagCriticalFieldChanged.TOPIC_TAG;
@NoArgsConstructor
public class TagDeleted extends DomainEvent {
    public TagDeleted(TagId tagId) {
        super(tagId);
        setTopic(TOPIC_TAG);
    }

    public TagDeleted(Set<TagId> tagIds) {
        super(tagIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
        setTopic(TOPIC_TAG);
    }
}
