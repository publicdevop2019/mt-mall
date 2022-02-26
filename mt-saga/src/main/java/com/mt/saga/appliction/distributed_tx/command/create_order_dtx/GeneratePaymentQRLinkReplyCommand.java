package com.mt.saga.appliction.distributed_tx.command.create_order_dtx;

import com.mt.saga.appliction.distributed_tx.command.ReplyEvent;
import lombok.Getter;

@Getter
public class GeneratePaymentQRLinkReplyCommand extends ReplyEvent {
    private String paymentLink;
}
