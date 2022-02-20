package com.mt.saga.appliction.order_state_machine;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderAddressCmdRep;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CommonOrderCommand extends DomainEvent {
    private String orderId;
    private String userId;
    private String txId;
    private BizOrderStatus orderState;
    private BizOrderEvent bizOrderEvent;
    private List<CartDetail> productList;
    private String createdBy;
    private BizOrderAddressCmdRep address;
    private BizOrderAddressCmdRep originalAddress;
    private long originalModifiedByUserAt;
    private String paymentType;
    private BigDecimal paymentAmt;
    private Integer version;

    public CommonOrderCommand() {
        setInternal(false);
    }

}
