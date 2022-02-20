package com.mt.saga.domain.model.reserve_order_dtx.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DecreaseOrderStorageForReserveEvent extends DomainEvent {
    public static final String name = "DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;

    public DecreaseOrderStorageForReserveEvent(ReserveOrderDTX dtx) {
        CommonOrderCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getOrderCommandDetail(), CommonOrderCommand.class);
        skuCommands = DomainRegistry.getProductService().getReserveOrderPatchCommands(deserialize.getProductList());
        this.changeId = dtx.getChangeId();
        this.orderId = dtx.getOrderId();
        this.taskId = dtx.getId();
        setInternal(false);
        setTopic(AppConstant.DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
