package com.mt.saga.domain.model.cancel_recycle_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderForRecycleEvent extends DomainEvent {
    public static final String name = "CANCEL_UPDATE_ORDER_FOR_RECYCLE_EVENT";
    private String changeId;
    private long taskId;
    private String orderId;
    private Integer orderVersion;
    public CancelUpdateOrderForRecycleEvent(CancelRecycleOrderDTX dtx) {
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.UPDATE_ORDER_FOR_RECYCLE_EVENT));
        setChangeId(dtx.getChangeId());
        setTaskId(dtx.getId());
        setOrderId(dtx.getOrderId());
        setOrderVersion(dtx.getOrderVersion());
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
