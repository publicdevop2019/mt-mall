package com.mt.saga.appliction.cancel_create_order_dtx.command;

import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import lombok.Getter;

@Getter
public class CancelDecreaseOrderStorageForCreateReplyCommand extends ReplyEvent {
}
