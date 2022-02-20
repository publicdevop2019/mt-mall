package com.mt.saga.domain.model.cancel_reserve_order_dtx.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CancelDecreaseOrderStorageForReserveEvent extends DomainEvent {
    public static final String name = "CANCEL_DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public CancelDecreaseOrderStorageForReserveEvent(CancelReserveOrderDTX dtx) {
        CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getOrderCommandDetail(), CommonOrderCommand.class);
        skuCommands = DomainRegistry.getProductService().getReserveOrderPatchCommands(command.getProductList());
        this.changeId = dtx.getChangeId();
        this.orderId = dtx.getOrderId();
        this.taskId = dtx.getId();
        setInternal(false);
        setDomainId(new DomainId(dtx.getId().toString()));
        setTopic(MQHelper.cancelOf(AppConstant.DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT));
        setName(name);
    }
}
