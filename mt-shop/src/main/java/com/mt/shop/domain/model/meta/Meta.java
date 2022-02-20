package com.mt.shop.domain.model.meta;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.shop.domain.model.tag.TagId;
import com.mt.shop.infrastructure.MetaChangeTagIdConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@NoArgsConstructor
@Getter
public class Meta extends Auditable {
    @Id
    private long id;
    @Setter(AccessLevel.PRIVATE)
    @Embedded
    private DomainId domainId;
    @Column(updatable = false)
    private MetaType type;
    private Boolean hasChangedTag;
    @Convert(converter = MetaChangeTagIdConverter.class)
    private Set<TagId> changedTagId;

    public void updateWarning(Long entityUpdateAt) {
        if (entityUpdateAt >= getModifiedAt().getTime())
            hasChangedTag = false;
    }

    public enum MetaType {
        CATALOG,
        FILTER,
        PRODUCT,
        SKU,
    }

    public Meta(DomainId domainId, MetaType type, Boolean hasChangedTag, Set<TagId> changedTagId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.domainId = domainId;
        this.type = type;
        this.hasChangedTag = hasChangedTag;
        this.changedTagId = changedTagId;
    }

    public void addChangeTag(TagId tagId) {
        if (changedTagId == null)
            changedTagId = new HashSet<>();
        changedTagId.add(tagId);
        hasChangedTag = true;
    }
}
