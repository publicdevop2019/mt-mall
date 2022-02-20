package com.mt.user_profile.domain.biz_order.event;

import com.mt.user_profile.application.biz_order.command.InternalAdminDeleteOrderEvent;
import com.mt.user_profile.application.biz_order.command.InternalCancelNewOrderEvent;
import com.mt.user_profile.domain.biz_order.BizOrderId;
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
