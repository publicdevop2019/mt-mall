package com.mt.saga.appliction.reserve_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.appliction.reserve_order_dtx.command.OrderUpdateForReserveFailedCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.order_state_machine.event.CreateReserveOrderDTXEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.DecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.UpdateOrderForReserveEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Slf4j
@Service
public class ReserveOrderDTXApplicationService {

    public static final String RESERVE_ORDER_DTX = "ReserveOrderDTX";

    @Transactional
    @SubscribeForEvent
    public void handle(CreateReserveOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                log.debug("start of creating {}", CreateReserveOrderDTXEvent.class.getName());
                LocalTx localTx1 = new LocalTx(DecreaseOrderStorageForReserveEvent.name, DecreaseOrderStorageForReserveEvent.name);
                LocalTx localTx2 = new LocalTx(UpdateOrderForReserveEvent.name, UpdateOrderForReserveEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                localTxes.add(localTx2);
                DistributedTx dtx = new DistributedTx(localTxes, "ReserveOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId());
                dtx.startAllLocalTx();
                dtx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new DecreaseOrderStorageForReserveEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainEventPublisher.instance().publish(new UpdateOrderForReserveEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainRegistry.getDistributedTxRepository().store(dtx);

            }, orderId);
            return null;
        }, RESERVE_ORDER_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(dtxId);
            byId.ifPresent(e -> e.cancel(AppConstant.RESERVE_ORDER_DTX_FAILED_EVENT));
            return null;
        }, "SystemCancelReserveOrderDtx");
    }

    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForReserveFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
