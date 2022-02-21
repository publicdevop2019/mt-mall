package com.mt.shop.domain.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CancelUpdateOrderPaymentSuccessReplyCommand extends DomainEvent {
    private long taskId;
    private boolean emptyOpt;
    public static final String name="CANCEL_UPDATE_ORDER_PAYMENT_SUCCESS_REPLY_COMMAND";
    public CancelUpdateOrderPaymentSuccessReplyCommand(long taskId, boolean emptyOpt) {
        this.taskId = taskId;
        this.emptyOpt = emptyOpt;
        setInternal(false);
        setTopic(MQHelper.replyCancelOf(AppConstant.UPDATE_ORDER_PAYMENT_SUCCESS_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}