package com.mt.saga.domain.model.distributed_tx.event.cancel_reserve_order_dtx;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.event.reserve_order_dtx.DecreaseOrderStorageForReserveEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CancelDecreaseOrderStorageForReserveEvent extends DomainEvent {
    public static final String name = MQHelper.cancelOf(DecreaseOrderStorageForReserveEvent.name);
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public CancelDecreaseOrderStorageForReserveEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        skuCommands = DomainRegistry.getProductService().getReserveOrderPatchCommands(command.getProductList());
        this.changeId = changeId;
        this.orderId = orderId;
        this.taskId = taskId;
        setInternal(false);
        setDomainId(new DomainId(taskId.toString()));
        setTopic(MQHelper.cancelOf(DecreaseOrderStorageForReserveEvent.name));
        setName(name);
    }
}
