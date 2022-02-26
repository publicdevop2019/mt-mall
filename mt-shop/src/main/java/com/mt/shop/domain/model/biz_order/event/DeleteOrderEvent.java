package com.mt.shop.domain.model.biz_order.event;

import com.mt.shop.application.biz_order.command.InternalAdminDeleteOrderEvent;
import com.mt.shop.application.biz_order.command.InternalCancelNewOrderEvent;
import com.mt.shop.domain.model.biz_order.BizOrderId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class DeleteOrderEvent {
    private BizOrderId orderId;
    private String userId;
    private String changeId;

    public DeleteOrderEvent(InternalCancelNewOrderEvent command) {
        setOrderId(command.getOrderId());
        setUserId(command.getUserId());
        setChangeId(command.getChangeId());
    }

    public DeleteOrderEvent(InternalAdminDeleteOrderEvent command) {
        setOrderId(command.getOrderId());
        setUserId(command.getUserId());
        setChangeId(command.getChangeId());
    }
}
