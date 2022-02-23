package com.mt.saga.appliction.recycle_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.appliction.recycle_order_dtx.command.OrderUpdateForRecycleFailedCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.order_state_machine.event.CreateRecycleOrderDTXEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.IncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.UpdateOrderForRecycleEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Service
@Slf4j
public class RecycleOrderDTXApplicationService {

    public static final String RECYCLE_ORDER_DTX = "RecycleOrderDTX";

    @Transactional
    @SubscribeForEvent
    public void handle(CreateRecycleOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                LocalTx localTx1 = new LocalTx(IncreaseOrderStorageForRecycleEvent.name, IncreaseOrderStorageForRecycleEvent.name);
                LocalTx localTx2 = new LocalTx(UpdateOrderForRecycleEvent.name, UpdateOrderForRecycleEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                localTxes.add(localTx2);
                DistributedTx dtx = new DistributedTx(localTxes, "RecycleOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId());
                dtx.startAllLocalTx();
                dtx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new IncreaseOrderStorageForRecycleEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainEventPublisher.instance().publish(new UpdateOrderForRecycleEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainRegistry.getDistributedTxRepository().store(dtx);


            }, orderId);
            return null;
        }, RECYCLE_ORDER_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        log.debug("start of cancel dtx {}", dtxId);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(dtxId);
            byId.ifPresent(e -> e.cancel(AppConstant.RECYCLE_ORDER_DTX_FAILED_EVENT));
            return null;
        }, "SystemCancelRecycleOrderDtx");
    }

    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForRecycleFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
