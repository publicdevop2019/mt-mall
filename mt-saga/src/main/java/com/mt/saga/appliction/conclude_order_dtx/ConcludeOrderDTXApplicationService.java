package com.mt.saga.appliction.conclude_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.conclude_order_dtx.event.DecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.UpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.order_state_machine.event.CreateConcludeOrderDTXEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Service
@Slf4j
public class ConcludeOrderDTXApplicationService {

    public static final String CONCLUDE_ORDER_DTX = "ConcludeOrderDTX";

    @Transactional
    @SubscribeForEvent
    public void handle(CreateConcludeOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {

                LocalTx localTx1 = new LocalTx(UpdateOrderForConcludeEvent.name, UpdateOrderForConcludeEvent.name);
                LocalTx localTx2 = new LocalTx(DecreaseActualStorageForConcludeEvent.name, DecreaseActualStorageForConcludeEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                localTxes.add(localTx2);
                DistributedTx distributedTx = new DistributedTx(localTxes, "ConcludeOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId(),AppConstant.CONCLUDE_ORDER_DTX_FAILED_EVENT);
                distributedTx.startLocalTx(UpdateOrderForConcludeEvent.name);
                distributedTx.startLocalTx(DecreaseActualStorageForConcludeEvent.name);
                distributedTx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new UpdateOrderForConcludeEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new DecreaseActualStorageForConcludeEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainRegistry.getDistributedTxRepository().store(distributedTx);

            }, orderId);
            return null;
        }, CONCLUDE_ORDER_DTX);
    }

}
