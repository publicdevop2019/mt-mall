package com.mt.saga.appliction;

import com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService;
import com.mt.saga.appliction.order_state_machine.OrderStateMachineApplicationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Getter
    private static OrderStateMachineApplicationService stateMachineApplicationService;
    @Getter
    private static DistributedTxApplicationService distributedTxApplicationService;


    @Getter
    private static PostDTXValidationApplicationService postDtxValidationApplicationService;

    @Autowired
    private void setPostDtxValidationApplicationService(PostDTXValidationApplicationService postDtxValidationApplicationService) {
        ApplicationServiceRegistry.postDtxValidationApplicationService = postDtxValidationApplicationService;
    }


    @Autowired
    private void setStateMachineApplicationService(OrderStateMachineApplicationService stateMachineApplicationService) {
        ApplicationServiceRegistry.stateMachineApplicationService = stateMachineApplicationService;
    }


    @Autowired
    private void setDistributedTxApplicationService(DistributedTxApplicationService distributedTxApplicationService) {
        ApplicationServiceRegistry.distributedTxApplicationService = distributedTxApplicationService;
    }

}
