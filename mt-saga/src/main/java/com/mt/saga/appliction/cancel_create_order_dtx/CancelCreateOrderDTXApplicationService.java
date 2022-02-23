package com.mt.saga.appliction.cancel_create_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelClearCartEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelDecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelGeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelSaveNewOrderEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Slf4j
@Service
public class CancelCreateOrderDTXApplicationService {

    private static final String CANCEL_CREATE_ORDER_DTX = "CancelCreateOrderDTX";

    @Transactional
    @SubscribeForEvent
    public void handle(DTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            LocalTx localTx1 = new LocalTx(CancelGeneratePaymentQRLinkEvent.name, CancelGeneratePaymentQRLinkEvent.name);
            LocalTx localTx2 = new LocalTx(CancelDecreaseOrderStorageForCreateEvent.name, CancelDecreaseOrderStorageForCreateEvent.name);
            LocalTx localTx3 = new LocalTx(CancelClearCartEvent.name, CancelClearCartEvent.name);
            LocalTx localTx4 = new LocalTx(CancelSaveNewOrderEvent.name, CancelSaveNewOrderEvent.name);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            localTxes.add(localTx2);
            localTxes.add(localTx3);
            localTxes.add(localTx4);
            DistributedTx dtx = DistributedTx.cancelOf(event.getDistributedTx(), localTxes);
            dtx.startAllLocalTx();
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get(DTX_COMMAND), CommonOrderCommand.class);
            log.info("start of cancel task of {} with changeId {}", dtx.getId(), dtx.getChangeId());
            DomainEventPublisher.instance().publish(new CancelGeneratePaymentQRLinkEvent(event.getDistributedTx().getLockId(), dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelDecreaseOrderStorageForCreateEvent(command, event.getDistributedTx().getLockId(), dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelClearCartEvent(command, dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelSaveNewOrderEvent(command, dtx.getChangeId(), dtx.getId()));
            dtx.startAllLocalTx();
            DomainRegistry.getDistributedTxRepository().store(dtx);
            return null;
        }, CANCEL_CREATE_ORDER_DTX);
    }
}
