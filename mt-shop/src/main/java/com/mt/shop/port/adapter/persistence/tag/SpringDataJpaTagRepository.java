package com.mt.shop.port.adapter.persistence.tag;

import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.shop.domain.model.tag.*;
import com.mt.shop.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SpringDataJpaTagRepository extends TagRepository, JpaRepository<Tag, Long> {

    default Optional<Tag> tagOfId(TagId tagOfId) {
        return getTagOfId(tagOfId);
    }

    private Optional<Tag> getTagOfId(TagId tagId) {
        return tagsOfQuery(new TagQuery(tagId)).findFirst();
    }

    default void add(Tag client) {
        save(client);
    }

    default void remove(Tag tag) {
        tag.softDelete();
        save(tag);
    }

    default void remove(Set<Tag> tags) {
        tags.forEach(Auditable::softDelete);
        saveAll(tags);
    }

    default SumPagedRep<Tag> tagsOfQuery(TagQuery tagQuery) {
        return QueryBuilderRegistry.getTagSelectQueryBuilder().execute(tagQuery);
    }


    @Component
    class JpaCriteriaApiTagAdaptor {
        public SumPagedRep<Tag> execute(TagQuery tagQuery) {
            QueryUtility.QueryContext<Tag> queryContext = QueryUtility.prepareContext(Tag.class, tagQuery);
            Optional.ofNullable(tagQuery.getTagIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(tagQuery.getTagIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Tag_.TAG_ID, queryContext));
            Optional.ofNullable(tagQuery.getName()).ifPresent(e -> QueryUtility.addStringLikePredicate(tagQuery.getName(), Tag_.NAME, queryContext));
            Optional.ofNullable(tagQuery.getType()).ifPresent(e -> QueryUtility.addStringEqualPredicate(tagQuery.getType().name(), Tag_.TYPE, queryContext));
            Order order = null;
            if (tagQuery.getTagSort().isById())
                order = QueryUtility.getDomainIdOrder(Tag_.TAG_ID, queryContext, tagQuery.getTagSort().isAsc());
            if (tagQuery.getTagSort().isByName())
                order = QueryUtility.getOrder(Tag_.NAME, queryContext, tagQuery.getTagSort().isAsc());
            if (tagQuery.getTagSort().isByType())
                order = QueryUtility.getOrder(Tag_.TYPE, queryContext, tagQuery.getTagSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(tagQuery, queryContext);
        }
    }
}
