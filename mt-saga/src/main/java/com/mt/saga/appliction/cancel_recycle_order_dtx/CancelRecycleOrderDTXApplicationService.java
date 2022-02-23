package com.mt.saga.appliction.cancel_recycle_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelIncreaseOrderStorageForRecycleReplyEvent;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelUpdateOrderForRecycleReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelIncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelUpdateOrderForRecycleEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mt.saga.appliction.create_order_dtx.CreateOrderDTXApplicationService.DTX_COMMAND;

@Service
@Slf4j
public class CancelRecycleOrderDTXApplicationService {

    private static final String CANCEL_RECYCLE_ORDER_DTX = "CancelRecycleOrderDTX";
    private static final String CANCEL_RECYCLE_ORDER_DTX_RESOLVED = "CancelRecycleOrderDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(DTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {

            LocalTx localTx1 = new LocalTx(CancelIncreaseOrderStorageForRecycleEvent.name, CancelIncreaseOrderStorageForRecycleEvent.name);
            LocalTx localTx2 = new LocalTx(CancelUpdateOrderForRecycleEvent.name, CancelUpdateOrderForRecycleEvent.name);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            localTxes.add(localTx2);
            DistributedTx dtx = DistributedTx.cancelOf(event.getDistributedTx(), localTxes);
            dtx.startAllLocalTx();
            DomainRegistry.getDistributedTxRepository().store(dtx);
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get(DTX_COMMAND), CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CancelIncreaseOrderStorageForRecycleEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForRecycleEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));

            return null;
        }, CANCEL_RECYCLE_ORDER_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderForRecycleReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(deserialize.getTaskId());
            byId.ifPresent(e -> {
                e.handle(CancelUpdateOrderForRecycleEvent.name, deserialize);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_RECYCLE_ORDER_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelIncreaseOrderStorageForRecycleReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(deserialize.getTaskId());
            byId.ifPresent(e -> {
                e.handle(CancelIncreaseOrderStorageForRecycleEvent.name, deserialize);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_RECYCLE_ORDER_DTX);
    }

    public SumPagedRep<DistributedTx> query(String queryParam, String pageParam, String skipCount) {
        DistributedTxQuery var0 = new DistributedTxQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getDistributedTxRepository().query(var0);
    }

    public Optional<DistributedTx> query(long id) {
        return DomainRegistry.getDistributedTxRepository().getById(id);
    }

    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(id);
            byId.ifPresent(e -> {
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_RECYCLE_ORDER_DTX_RESOLVED);
    }
}
