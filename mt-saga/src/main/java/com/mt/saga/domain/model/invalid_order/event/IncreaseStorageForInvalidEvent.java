package com.mt.saga.domain.model.invalid_order.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
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

    public IncreaseStorageForInvalidEvent(InvalidOrderDTX dtx) {
        CommonOrderCommand deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getOrderCommandDetail(), CommonOrderCommand.class);
        List<PatchCommand> reserveOrderPatchCommands = DomainRegistry.getProductService().getReserveOrderPatchCommands(deserialize.getProductList());
        if(deserialize.getOrderState().equals(BizOrderStatus.CONFIRMED)){
            List<PatchCommand> commands = DomainRegistry.getProductService().getConfirmOrderPatchCommands(deserialize.getProductList());
            reserveOrderPatchCommands.addAll(commands);
        }
        skuCommands = ProductService.buildRollbackCommand(reserveOrderPatchCommands);
        this.changeId = dtx.getChangeId();
        this.orderId = dtx.getOrderId();
        this.taskId = dtx.getId();
        setInternal(false);
        setTopic(AppConstant.INCREASE_STORAGE_FOR_INVALID_EVENT);
        setDomainId(new DomainId(dtx.getId().toString()));
        setName(name);
    }
}
