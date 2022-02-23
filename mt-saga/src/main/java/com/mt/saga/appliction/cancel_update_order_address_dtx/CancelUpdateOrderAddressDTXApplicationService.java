package com.mt.saga.appliction.cancel_update_order_address_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_update_order_address_dtx.command.CancelUpdateOrderAddressReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mt.saga.appliction.create_order_dtx.CreateOrderDTXApplicationService.DTX_COMMAND;

@Service
public class CancelUpdateOrderAddressDTXApplicationService {
    private static final String CANCEL_UPDATE_ORDER_ADDRESS_DTX = "CancelUpdateOrderAddressDTX";
    private static final String CANCEL_UPDATE_ORDER_ADDRESS_DTX_RESOLVED = "CancelUpdateOrderAddressDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(DTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            LocalTx localTx1 = new LocalTx(CancelUpdateOrderForUpdateOrderAddressEvent.name, CancelUpdateOrderForUpdateOrderAddressEvent.name);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            DistributedTx dtx = DistributedTx.cancelOf(event.getDistributedTx(), localTxes);
            dtx.startAllLocalTx();
            DomainRegistry.getDistributedTxRepository().store(dtx);
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get(DTX_COMMAND), CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForUpdateOrderAddressEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));

            return null;
        }, CANCEL_UPDATE_ORDER_ADDRESS_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderAddressReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(deserialize.getTaskId());
            byId.ifPresent(e -> {
                e.handle(CancelUpdateOrderForUpdateOrderAddressEvent.name, deserialize);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_UPDATE_ORDER_ADDRESS_DTX);
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
        }, CANCEL_UPDATE_ORDER_ADDRESS_DTX_RESOLVED);
    }
}
