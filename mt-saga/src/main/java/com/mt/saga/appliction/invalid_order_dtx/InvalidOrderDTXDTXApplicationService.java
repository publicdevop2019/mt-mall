package com.mt.saga.appliction.invalid_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.invalid_order.event.IncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RestoreCartForInvalidEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateInvalidOrderDTXEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Slf4j
@Service
public class InvalidOrderDTXDTXApplicationService {
    private static final String INVALID_ORDER_DTX = "INVALID_ORDER_DTX";


    @Transactional
    @SubscribeForEvent
    public void handle(CreateInvalidOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            CommonOrderCommand command = event.getCommand();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                Set<LocalTx> localTxes = new HashSet<>();
                LocalTx localTx1 = new LocalTx(RemovePaymentQRLinkForInvalidEvent.name, RemovePaymentQRLinkForInvalidEvent.name);
                LocalTx localTx2 = new LocalTx(RestoreCartForInvalidEvent.name, RestoreCartForInvalidEvent.name);
                LocalTx localTx3 = new LocalTx(RemoveOrderForInvalidEvent.name, RemoveOrderForInvalidEvent.name);
                LocalTx localTx4 = new LocalTx(IncreaseStorageForInvalidEvent.name, IncreaseStorageForInvalidEvent.name);
                localTxes.add(localTx1);
                localTxes.add(localTx2);
                localTxes.add(localTx3);
                localTxes.add(localTx4);
                DistributedTx distributedTx = new DistributedTx(localTxes, "InvalidOrderDtx", command.getTxId(), command.getOrderId(), AppConstant.INVALID_ORDER_DTX_FAILED_EVENT);
                distributedTx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                if (command.getOrderState().equals(BizOrderStatus.PAID_RECYCLED)) {

                    distributedTx.startLocalTx(RemovePaymentQRLinkForInvalidEvent.name);
                    distributedTx.startLocalTx(RestoreCartForInvalidEvent.name);
                    distributedTx.startLocalTx(RemoveOrderForInvalidEvent.name);
                    distributedTx.skipLocalTx(IncreaseStorageForInvalidEvent.name);

                    DomainEventPublisher.instance().publish(new RemovePaymentQRLinkForInvalidEvent(command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));
                } else if (command.getOrderState().equals(BizOrderStatus.PAID_RESERVED) || command.getOrderState().equals(BizOrderStatus.CONFIRMED)) {

                    distributedTx.startLocalTx(RemovePaymentQRLinkForInvalidEvent.name);
                    distributedTx.startLocalTx(RestoreCartForInvalidEvent.name);
                    distributedTx.startLocalTx(RemoveOrderForInvalidEvent.name);
                    distributedTx.startLocalTx(IncreaseStorageForInvalidEvent.name);

                    DomainEventPublisher.instance().publish(new RemovePaymentQRLinkForInvalidEvent(command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new IncreaseStorageForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));

                } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RESERVED)) {

                    distributedTx.skipLocalTx(RemovePaymentQRLinkForInvalidEvent.name);
                    distributedTx.startLocalTx(RestoreCartForInvalidEvent.name);
                    distributedTx.startLocalTx(RemoveOrderForInvalidEvent.name);
                    distributedTx.startLocalTx(IncreaseStorageForInvalidEvent.name);

                    DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new IncreaseStorageForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));

                } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RECYCLED)) {

                    distributedTx.skipLocalTx(RemovePaymentQRLinkForInvalidEvent.name);
                    distributedTx.startLocalTx(RestoreCartForInvalidEvent.name);
                    distributedTx.startLocalTx(RemoveOrderForInvalidEvent.name);
                    distributedTx.skipLocalTx(IncreaseStorageForInvalidEvent.name);

                    DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));
                    DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(command, command.getOrderId(), command.getTxId(), distributedTx.getId()));

                }

                DomainRegistry.getDistributedTxRepository().store(distributedTx);
            }, command.getOrderId());
            return null;
        }, INVALID_ORDER_DTX);
    }

}
