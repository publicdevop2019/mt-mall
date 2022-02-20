package com.mt.saga.domain.model.conclude_order_dtx.event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class DecreaseActualStorageForConcludeEvent extends DomainEvent {
    public static final String name = "DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public DecreaseActualStorageForConcludeEvent(ConcludeOrderDTX dtx) {
        CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getCreateBizStateMachineCommand(), CommonOrderCommand.class);
        this.skuCommands = DomainRegistry.getProductService().getConfirmOrderPatchCommands(command.getProductList());
        this.changeId = dtx.getChangeId();
        this.orderId = dtx.getOrderId();
        this.taskId = dtx.getId();
        setInternal(false);
        setDomainId(new DomainId(dtx.getId().toString()));
        setTopic(AppConstant.DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT);
        setName(name);
    }
}
