package com.mt.shop.domain.biz_order.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.shop.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderPaymentSuccessReplyEvent extends DomainEvent {
    private boolean emptyOpt;
    private long taskId;
    public static final String name="UPDATE_ORDER_PAYMENT_SUCCESS_REPLY_EVENT";
    public UpdateOrderPaymentSuccessReplyEvent(boolean emptyOpt, long taskId) {
        this.emptyOpt = emptyOpt;
        this.taskId = taskId;
        setInternal(false);
        setTopic(MQHelper.replyOf(AppConstant.UPDATE_ORDER_PAYMENT_SUCCESS_EVENT));
        setDomainId(new DomainId(String.valueOf(taskId)));
        setName(name);
    }
}
