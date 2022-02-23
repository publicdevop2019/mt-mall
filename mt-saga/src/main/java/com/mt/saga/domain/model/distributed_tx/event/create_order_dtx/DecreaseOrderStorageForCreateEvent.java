package com.mt.saga.domain.model.distributed_tx.event.create_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DecreaseOrderStorageForCreateEvent extends DomainEvent {
    public static final String name = "DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;

    public DecreaseOrderStorageForCreateEvent(String orderId, String changeId, Long taskId, CommonOrderCommand command) {
        skuCommands = DomainRegistry.getProductService().getReserveOrderPatchCommands(command.getProductList());
        this.changeId = changeId;
        this.orderId = orderId;
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
