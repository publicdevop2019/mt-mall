package com.mt.shop.domain.model.sku.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.Setter;

import static com.mt.shop.infrastructure.AppConstant.SAGA_REPLY_EVENT;

@Getter
@Setter
public class SkuPatchedReplyEvent extends DomainEvent {
    private boolean emptyOpt;
    private long taskId;
    private String replyOf;
    public static final String name="SKU_PATCHED_REPLY_EVENT";

    public SkuPatchedReplyEvent(boolean emptyOpt, long taskId,String replyOf) {
        setEmptyOpt(emptyOpt);
        setTaskId(taskId);
        setInternal(false);
        setTopic(SAGA_REPLY_EVENT);
        setDomainId(new DomainId(String.valueOf(taskId)));
        this.replyOf = replyOf;
        setName(name);
    }
}
