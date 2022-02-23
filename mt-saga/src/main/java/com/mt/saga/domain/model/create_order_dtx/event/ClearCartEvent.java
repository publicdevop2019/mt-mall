package com.mt.saga.domain.model.create_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ClearCartEvent extends DomainEvent {
    public static final String name = "CLEAR_CART_EVENT";
    private String userId;
    private Set<String> ids;
    private String changeId;
    private long taskId;


    public ClearCartEvent(CommonOrderCommand command, String changeId, Long taskId) {
        Set<String> cartIds = command.getProductList().stream().map(CartDetail::getCartId).collect(Collectors.toSet());
        this.userId = command.getUserId();
        this.ids = cartIds;
        this.changeId = changeId;
        this.taskId = taskId;
        setName(name);
        setDomainId(new DomainId(taskId.toString()));
        setInternal(false);
        setTopic(AppConstant.CLEAR_CART_FOR_CREATE_EVENT);
        setName(name);
    }
}
