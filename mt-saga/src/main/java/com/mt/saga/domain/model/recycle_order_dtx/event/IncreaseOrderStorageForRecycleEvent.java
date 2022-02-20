package com.mt.saga.domain.model.recycle_order_dtx.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.order_state_machine.ProductService;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class IncreaseOrderStorageForRecycleEvent extends DomainEvent {
    public static final String name = "INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public IncreaseOrderStorageForRecycleEvent(RecycleOrderDTX dtx) {
        CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getCreateBizStateMachineCommand(), CommonOrderCommand.class);
        this.skuCommands = ProductService.buildRollbackCommand(DomainRegistry.getProductService().getReserveOrderPatchCommands(command.getProductList()));
        this.changeId = dtx.getChangeId();
        this.orderId = dtx.getOrderId();
        this.taskId = dtx.getId();
        setInternal(false);
        setTopic(AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }

}
