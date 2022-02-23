package com.mt.saga.domain.model.distributed_tx;


import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Data;

@Data
public class DTXSuccessEvent extends DomainEvent {
    public static final String name = "DTX_SUCCESS_EVENT";
    private String orderId;
    private long taskId;
    private long originalTaskId;
    private String changeId;
    private boolean isCancel;

    public DTXSuccessEvent(DistributedTx dtx) {
        setOrderId(dtx.getLockId());
        setInternal(true);
        setTopic(AppConstant.DTX_SUCCESS_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
        setTaskId(dtx.getId());
        setChangeId(dtx.getChangeId());
        isCancel = dtx.isCancel();
        if (dtx.isCancel()) {
            originalTaskId = dtx.getForwardDtxId();
        }
    }

    private DTXSuccessEvent() {
    }
}
