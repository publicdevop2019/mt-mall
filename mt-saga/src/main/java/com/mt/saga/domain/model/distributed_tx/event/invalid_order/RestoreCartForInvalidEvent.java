package com.mt.saga.domain.model.distributed_tx.event.invalid_order;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RestoreCartForInvalidEvent extends DomainEvent {
    public static final String name = "RESTORE_CART_EVENT";
    private String userId;
    private Set<String> ids;
    private String changeId;
    private long taskId;
    private Map<String, Integer> idVersionMap;

    public RestoreCartForInvalidEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        Set<String> cartIds = command.getProductList().stream().map(CartDetail::getCartId).collect(Collectors.toSet());
        this.userId = command.getUserId();
        this.ids = cartIds;
        this.changeId = changeId;
        this.taskId = taskId;
        setName(name);
        setDomainId(new DomainId(taskId.toString()));
        setInternal(false);
        setTopic(AppConstant.RESTORE_CART_FOR_INVALID_EVENT);
        setName(name);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        command.getProductList().forEach(e -> {
            stringIntegerHashMap.put(e.getCartId(), e.getVersion());
        });
        setIdVersionMap(stringIntegerHashMap);
    }
}
