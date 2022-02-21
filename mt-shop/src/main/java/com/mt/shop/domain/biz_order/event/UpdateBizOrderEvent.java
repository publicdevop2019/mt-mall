package com.mt.shop.domain.biz_order.event;

import com.mt.shop.application.biz_order.command.InternalUpdateBizOrderCommand;
import com.mt.shop.domain.biz_order.BizOrderId;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Date;

@Getter
@ToString
public class UpdateBizOrderEvent {
    private static final long serialVersionUID = 1;
    private final BizOrderId orderId;
    private final Integer version;
    private final String changeId;
    private final Boolean orderStorage;
    private final Boolean actualStorage;
    private final Boolean paid;
    private final Boolean deleted;
    private final String deletedBy;
    private final Date deletedAt;

    public UpdateBizOrderEvent(InternalUpdateBizOrderCommand command) {
        orderId = command.getOrderId();
        version = command.getVersion();
        changeId = command.getChangeId();
        orderStorage = command.getOrderStorage();
        actualStorage = command.getActualStorage();
        paid = command.getPaid();
        deleted = command.getDeleted();
        deletedAt = Date.from(Instant.now());
        deletedBy = command.getDeletedBy();
    }
}
