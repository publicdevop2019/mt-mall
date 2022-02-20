package com.mt.saga.domain.model.reserve_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateOrderForReserveEvent extends DomainEvent {
    public static final String name = "UPDATE_ORDER_FOR_RESERVE_EVENT";
    private String orderId;
    private long taskId;
    private String changeId;
    private Integer orderVersion;

    public UpdateOrderForReserveEvent(ReserveOrderDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.taskId = dtx.getId();
        this.changeId = dtx.getChangeId();
        setInternal(false);
        setTopic(AppConstant.UPDATE_ORDER_FOR_RESERVE_EVENT);
        setOrderVersion(dtx.getOrderVersion());
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
