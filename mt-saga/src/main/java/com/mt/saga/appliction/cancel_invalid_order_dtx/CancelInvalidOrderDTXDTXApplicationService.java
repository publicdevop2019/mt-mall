package com.mt.saga.appliction.cancel_invalid_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelIncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelRemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelRemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelRestoreCartForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;

@Service
public class CancelInvalidOrderDTXDTXApplicationService {
    private static final String CANCEL_INVALID_ORDER_DTX = "CANCEL_INVALID_ORDER_DTX";

    @Transactional
    @SubscribeForEvent
    public void handle(DTXFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getDistributedTx().getParameters().get("COMMAND"), CommonOrderCommand.class);
            HashSet<LocalTx> localTxes = new HashSet<>();
            LocalTx localTx1 = new LocalTx(CancelRemovePaymentQRLinkForInvalidEvent.name, CancelRemovePaymentQRLinkForInvalidEvent.name);
            LocalTx localTx2 = new LocalTx(CancelRestoreCartForInvalidEvent.name, CancelRestoreCartForInvalidEvent.name);
            LocalTx localTx3 = new LocalTx(CancelRemoveOrderForInvalidEvent.name, CancelRemoveOrderForInvalidEvent.name);
            LocalTx localTx4 = new LocalTx(CancelIncreaseStorageForInvalidEvent.name, CancelIncreaseStorageForInvalidEvent.name);
            localTxes.add(localTx1);
            localTxes.add(localTx2);
            localTxes.add(localTx3);
            localTxes.add(localTx4);
            DistributedTx distributedTx = DistributedTx.cancelOf(event.getDistributedTx(), localTxes);

            if (command.getOrderState().equals(BizOrderStatus.PAID_RECYCLED)) {
                DomainEventPublisher.instance().publish(new CancelRemovePaymentQRLinkForInvalidEvent(distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));

                distributedTx.skipLocalTx(CancelIncreaseStorageForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRestoreCartForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRemoveOrderForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRemovePaymentQRLinkForInvalidEvent.name);
            } else if (command.getOrderState().equals(BizOrderStatus.PAID_RESERVED) || command.getOrderState().equals(BizOrderStatus.CONFIRMED)) {
                DomainEventPublisher.instance().publish(new CancelRemovePaymentQRLinkForInvalidEvent(distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelIncreaseStorageForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));

                distributedTx.startLocalTx(CancelIncreaseStorageForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRestoreCartForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRemoveOrderForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRemovePaymentQRLinkForInvalidEvent.name);
            } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RESERVED)) {
                DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelIncreaseStorageForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));

                distributedTx.skipLocalTx(CancelRemovePaymentQRLinkForInvalidEvent.name);
                distributedTx.startLocalTx(CancelIncreaseStorageForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRestoreCartForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRemoveOrderForInvalidEvent.name);

            } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RECYCLED)) {
                DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));

                distributedTx.skipLocalTx(CancelIncreaseStorageForInvalidEvent.name);
                distributedTx.skipLocalTx(CancelRemovePaymentQRLinkForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRestoreCartForInvalidEvent.name);
                distributedTx.startLocalTx(CancelRemoveOrderForInvalidEvent.name);

            }
            DomainRegistry.getDistributedTxRepository().store(distributedTx);
            return null;
        }, CANCEL_INVALID_ORDER_DTX);
    }
}
