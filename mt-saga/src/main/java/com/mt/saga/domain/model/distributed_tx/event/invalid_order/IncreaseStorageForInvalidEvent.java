package com.mt.saga.domain.model.distributed_tx.event.invalid_order;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.order_state_machine.ProductService;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class IncreaseStorageForInvalidEvent extends DomainEvent {
    public static final String name = "INCREASE_STORAGE_FOR_INVALID_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;

    public IncreaseStorageForInvalidEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        List<PatchCommand> reserveOrderPatchCommands = DomainRegistry.getProductService().getReserveOrderPatchCommands(command.getProductList());
        if (command.getOrderState().equals(BizOrderStatus.CONFIRMED)) {
            List<PatchCommand> commands = DomainRegistry.getProductService().getConfirmOrderPatchCommands(command.getProductList());
            reserveOrderPatchCommands.addAll(commands);
        }
        skuCommands = ProductService.buildRollbackCommand(reserveOrderPatchCommands);
        this.changeId = changeId;
        this.orderId = orderId;
        this.taskId = taskId;
        setInternal(false);
        setTopic(AppConstant.INCREASE_STORAGE_FOR_INVALID_EVENT);
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
