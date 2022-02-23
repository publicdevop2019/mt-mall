package com.mt.saga.domain.model.cancel_conclude_order_dtx.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
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
public class CancelDecreaseActualStorageForConcludeEvent extends DomainEvent {
    public static final String name = "CANCEL_DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT";
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;


    public CancelDecreaseActualStorageForConcludeEvent(CommonOrderCommand command, String orderId, String changeId, Long taskId) {
        this.skuCommands = DomainRegistry.getProductService().getConfirmOrderPatchCommands(command.getProductList());
        this.changeId = changeId;
        this.orderId = orderId;
        this.taskId = taskId;
        setName(name);
        setDomainId(new DomainId(taskId.toString()));
        setInternal(false);
        setTopic(MQHelper.cancelOf(AppConstant.DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT));
    }
}
