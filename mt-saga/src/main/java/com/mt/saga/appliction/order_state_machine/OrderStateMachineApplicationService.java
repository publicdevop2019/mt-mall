package com.mt.saga.appliction.order_state_machine;

import com.mt.common.application.CommonApplicationServiceRegistry;

import com.mt.saga.domain.DomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderStateMachineApplicationService {

    public static final String STATE_MACHINE = "StateMachine";

    @Transactional
    
    public void start(CommonOrderCommand event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getTxId(), (command) -> {
            DomainRegistry.getOrderStateMachineBuilder().handle(event);
            return null;
        }, STATE_MACHINE);
    }

}
