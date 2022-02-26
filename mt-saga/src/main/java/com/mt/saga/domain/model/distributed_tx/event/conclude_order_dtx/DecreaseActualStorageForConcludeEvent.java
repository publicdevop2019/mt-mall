package com.mt.saga.domain.model.distributed_tx.event.conclude_order_dtx;

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

@Setter
@Getter
@NoArgsConstructor
public class DecreaseActualStorageForConcludeEvent extends DomainEvent {
    public static final String name = AppConstant.DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT;
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public DecreaseActualStorageForConcludeEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {

        this.skuCommands = DomainRegistry.getProductService().getConfirmOrderPatchCommands(command.getProductList());
        this.changeId = changeId;
        this.orderId = orderId;
        this.taskId = taskId;
        setInternal(false);
        setDomainId(new DomainId(taskId.toString()));
        setTopic(AppConstant.DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT);
        setName(name);
    }
}
