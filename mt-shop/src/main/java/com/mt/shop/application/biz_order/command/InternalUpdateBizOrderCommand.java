package com.mt.shop.application.biz_order.command;

import com.mt.shop.domain.model.biz_order.BizOrderId;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
public class InternalUpdateBizOrderCommand {
    private static final long serialVersionUID = 1;
    @TargetAggregateIdentifier
    private BizOrderId orderId;
    private Integer version;
    private String changeId;
    private String userId;
    private Boolean orderStorage;
    private Boolean actualStorage;
    private Boolean paid;
    private Boolean deleted;
    private String deletedBy;


    public InternalUpdateBizOrderCommand(UpdateOrderForRecycleCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion();
        orderStorage = false;
    }

    public InternalUpdateBizOrderCommand(UpdateOrderForReserveCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion();
        orderStorage = true;
    }

    public InternalUpdateBizOrderCommand(UpdateOrderPaymentSuccessCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion();
        paid = true;
    }

    public InternalUpdateBizOrderCommand(UpdateOrderForConcludeCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion();
        actualStorage = true;
    }

    public InternalUpdateBizOrderCommand(CancelUpdateOrderForRecycleCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion() + 1;//if ltx success then version should +1
        orderStorage = true;
    }

    public InternalUpdateBizOrderCommand(CancelUpdateOrderForReserveCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion() + 1;//if ltx success then version should +1
        orderStorage = false;
    }

    public InternalUpdateBizOrderCommand(CancelUpdateOrderForConcludeCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion() + 1;//if ltx success then version should +1
        actualStorage = false;
    }

    public InternalUpdateBizOrderCommand(CancelUpdateOrderPaymentSuccessCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion() + 1;//if ltx success then version should +1
        paid = false;
    }

    public InternalUpdateBizOrderCommand(UpdateOrderForInvalidCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion();
        deleted = true;
        deletedBy = command.getUserId();
    }

    public InternalUpdateBizOrderCommand(CancelUpdateOrderForInvalidCommand command) {
        this.changeId = command.getChangeId();
        orderId = new BizOrderId(command.getOrderId());
        version = command.getOrderVersion() + 1;//if ltx success then version should +1
        deleted = false;
    }
}
