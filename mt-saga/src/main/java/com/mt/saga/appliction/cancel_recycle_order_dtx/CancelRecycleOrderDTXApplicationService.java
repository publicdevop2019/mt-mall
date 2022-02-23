package com.mt.saga.appliction.cancel_recycle_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelIncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelUpdateOrderForRecycleEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxFailedEvent;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Service
@Slf4j
public class CancelRecycleOrderDTXApplicationService {

    private static final String CANCEL_RECYCLE_ORDER_DTX = "CancelRecycleOrderDTX";

    @Transactional
    @SubscribeForEvent
    public void handle(DistributedTxFailedEvent event) {
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
}
