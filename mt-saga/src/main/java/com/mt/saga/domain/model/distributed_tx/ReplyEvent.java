package com.mt.saga.domain.model.distributed_tx;

import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelClearCartReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelDecreaseOrderStorageForCreateReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelGeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelSaveNewOrderReplyCommand;
import com.mt.saga.appliction.create_order_dtx.command.ClearCartReplyCommand;
import com.mt.saga.appliction.create_order_dtx.command.DecreaseOrderStorageForCreateReplyCommand;
import com.mt.saga.appliction.create_order_dtx.command.GeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.create_order_dtx.command.SaveNewOrderReplyCommand;
import lombok.Getter;

@Getter
public class ReplyEvent {
    private final Long taskId;
    private final boolean isEmptyOperation;

    public ReplyEvent(ClearCartReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }

    public ReplyEvent(DecreaseOrderStorageForCreateReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }

    public ReplyEvent(GeneratePaymentQRLinkReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }

    public ReplyEvent(SaveNewOrderReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }

    public ReplyEvent(CancelGeneratePaymentQRLinkReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }

    public ReplyEvent(CancelDecreaseOrderStorageForCreateReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }

    public ReplyEvent(CancelClearCartReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }

    public ReplyEvent(CancelSaveNewOrderReplyCommand command) {
        taskId = command.getTaskId();
        isEmptyOperation = command.isEmptyOpt();
    }
}
