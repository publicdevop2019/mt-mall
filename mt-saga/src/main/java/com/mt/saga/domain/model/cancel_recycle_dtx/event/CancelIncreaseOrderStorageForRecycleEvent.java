package com.mt.saga.domain.model.cancel_recycle_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
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
public class CancelIncreaseOrderStorageForRecycleEvent extends DomainEvent {
    public static final String name = "CANCEL_INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public CancelIncreaseOrderStorageForRecycleEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.skuCommands = ProductService.buildRollbackCommand(DomainRegistry.getProductService().getReserveOrderPatchCommands(command.getProductList()));
        this.changeId = changeId;
        this.taskId = taskId;
        this.orderId = orderId;
        setDomainId(new DomainId(taskId.toString()));
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT));
        setName(name);
    }

}
