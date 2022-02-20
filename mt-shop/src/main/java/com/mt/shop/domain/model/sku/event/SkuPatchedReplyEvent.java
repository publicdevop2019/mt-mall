package com.mt.shop.domain.model.sku.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkuPatchedReplyEvent extends DomainEvent {
    private boolean emptyOpt;
    private long taskId;
    public static final String name="SKU_PATCHED_REPLY_EVENT";

    public SkuPatchedReplyEvent(boolean emptyOpt, long taskId,String topic) {
        setEmptyOpt(emptyOpt);
        setTaskId(taskId);
        setInternal(false);
        setTopic(topic);
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
