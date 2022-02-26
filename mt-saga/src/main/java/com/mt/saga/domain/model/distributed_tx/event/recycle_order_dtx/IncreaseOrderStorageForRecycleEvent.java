package com.mt.saga.domain.model.distributed_tx.event.recycle_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.order_state_machine.ProductService;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class IncreaseOrderStorageForRecycleEvent extends DomainEvent {
    public static final String name = AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT;
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public IncreaseOrderStorageForRecycleEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.skuCommands = ProductService.buildRollbackCommand(DomainRegistry.getProductService().getReserveOrderPatchCommands(command.getProductList()));
        this.changeId = changeId;
        this.orderId = orderId;
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }

}
