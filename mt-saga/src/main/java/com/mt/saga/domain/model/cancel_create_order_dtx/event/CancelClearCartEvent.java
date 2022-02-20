package com.mt.saga.domain.model.cancel_create_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class CancelClearCartEvent extends DomainEvent {
    public static final String name = "CANCEL_CLEAR_CART_EVENT";
    private String userId;
    private Set<String> ids;
    private Map<String,Integer> idVersionMap;
    private String changeId;
    private long taskId;


    public CancelClearCartEvent(CommonOrderCommand command, CancelCreateOrderDTX dtx) {
        Set<String> collect = command.getProductList().stream().map(CartDetail::getCartId).collect(Collectors.toSet());
        this.userId = command.getUserId();
        this.ids = collect;
        this.changeId = dtx.getChangeId();
        this.taskId = dtx.getId();
        setDomainId(new DomainId(dtx.getId().toString()));
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.CLEAR_CART_FOR_CREATE_EVENT));
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        command.getProductList().forEach(e->{
            stringIntegerHashMap.put(e.getCartId(),e.getVersion());
        });
        setIdVersionMap(stringIntegerHashMap);
        setName(name);
    }
}
