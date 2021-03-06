package com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.event.invalid_order.IncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CancelIncreaseStorageForInvalidEvent extends DomainEvent {
    public static final String name = MQHelper.cancelOf(IncreaseStorageForInvalidEvent.name);
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;

    public CancelIncreaseStorageForInvalidEvent(CommonOrderCommand deserialize, String orderId, String changeId, Long taskId) {
        List<PatchCommand> reserveOrderPatchCommands = DomainRegistry.getProductService().getReserveOrderPatchCommands(deserialize.getProductList());
        if (deserialize.getOrderState().equals(BizOrderStatus.CONFIRMED)) {
            List<PatchCommand> commands = DomainRegistry.getProductService().getConfirmOrderPatchCommands(deserialize.getProductList());
            reserveOrderPatchCommands.addAll(commands);
        }
        skuCommands = reserveOrderPatchCommands;
        this.changeId = changeId;
        this.orderId = orderId;
        this.taskId = taskId;
        setInternal(false);
        setTopic(MQHelper.cancelOf(IncreaseStorageForInvalidEvent.name));
        setDomainId(new DomainId(taskId.toString()));
        setName(name);
    }
}
