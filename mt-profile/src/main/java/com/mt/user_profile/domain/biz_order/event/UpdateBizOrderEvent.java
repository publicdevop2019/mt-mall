package com.mt.user_profile.domain.biz_order.event;

import com.mt.user_profile.application.biz_order.command.InternalUpdateBizOrderCommand;
import com.mt.user_profile.domain.biz_order.BizOrderId;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Date;

@Getter
@ToString
public class UpdateBizOrderEvent {
    private static final long serialVersionUID = 1;
    private BizOrderId orderId;
    private Integer version;
    private String changeId;
    private Boolean orderStorage;
    private Boolean actualStorage;
    private Boolean paid;
    private Boolean deleted;
    private String deletedBy;
    private Date deletedAt;

    public UpdateBizOrderEvent(InternalUpdateBizOrderCommand command) {
        orderId = command.getOrderId();
        version = command.getVersion();
        changeId = command.getChangeId();
        orderStorage = command.getOrderStorage();
        actualStorage = command.getActualStorage();
        paid = command.getPaid();
        deleted = command.getDeleted();
        deletedAt = Date.from(Instant.now());
        deletedBy =command.getDeletedBy();
    }
}
