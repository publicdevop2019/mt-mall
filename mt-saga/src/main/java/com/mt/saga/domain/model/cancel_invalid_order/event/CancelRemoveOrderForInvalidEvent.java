package com.mt.saga.domain.model.cancel_invalid_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CancelRemoveOrderForInvalidEvent extends DomainEvent {
    public static final String name = "CANCEL_REMOVE_ORDER_FOR_INVALID_EVENT";
    private String orderId;
    private String changeId;
    private long taskId;
    private Integer orderVersion;

    public CancelRemoveOrderForInvalidEvent(CancelInvalidOrderDTX dtx) {
        this.orderId = dtx.getOrderId();
        this.changeId = dtx.getChangeId();
        this.taskId = dtx.getId();
        setOrderVersion(dtx.getOrderVersion());
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.REMOVE_ORDER_FOR_INVALID_EVENT));
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }

}
