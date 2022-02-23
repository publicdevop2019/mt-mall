package com.mt.saga.appliction.distributed_tx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.create_order_dtx.command.GeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.distributed_tx.command.ResolveReason;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.event.cancel_create_order_dtx.CancelClearCartEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_create_order_dtx.CancelDecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_create_order_dtx.CancelGeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_create_order_dtx.CancelSaveNewOrderEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelIncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelRemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelRemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_invalid_order.CancelRestoreCartForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_recycle_dtx.CancelIncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_recycle_dtx.CancelUpdateOrderForRecycleEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_reserve_order_dtx.CancelDecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_reserve_order_dtx.CancelUpdateOrderForReserveEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_update_order_address_dtx.CancelUpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.domain.model.distributed_tx.event.conclude_order_dtx.DecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.distributed_tx.event.conclude_order_dtx.UpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.distributed_tx.event.confirm_order_payment_dtx.UpdateOrderPaymentSuccessEvent;
import com.mt.saga.domain.model.distributed_tx.event.create_order_dtx.ClearCartEvent;
import com.mt.saga.domain.model.distributed_tx.event.create_order_dtx.DecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.distributed_tx.event.create_order_dtx.GeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.distributed_tx.event.create_order_dtx.SaveNewOrderEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import com.mt.saga.domain.model.distributed_tx.event.DistributedTxFailedEvent;
import com.mt.saga.domain.model.distributed_tx.event.DistributedTxSuccessEvent;
import com.mt.saga.domain.model.distributed_tx.event.LocalTxFailedEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_conclude_order_dtx.CancelDecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_conclude_order_dtx.CancelUpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.distributed_tx.event.cancel_confirm_order_payment_dtx.CancelUpdateOrderPaymentEvent;
import com.mt.saga.domain.model.invalid_order.event.IncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RestoreCartForInvalidEvent;
import com.mt.saga.domain.model.order_state_machine.event.*;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.recycle_order_dtx.event.IncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.UpdateOrderForRecycleEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.DecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.UpdateOrderForReserveEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class DistributedTxApplicationService {

    public static final String DTX_COMMAND = "COMMAND";
    public static final String RESOLVE_DTX = "RESOLVE_DTX";
    private static final String CREATE_DTX = "CreateDtx";
    public static final String CANCEL_DTX = "CancelDtx";

    @Transactional
    @SubscribeForEvent
    public void concludeOrderFailed(DistributedTxFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {

            LocalTx localTx1 = new LocalTx(CancelUpdateOrderForConcludeEvent.name, CancelUpdateOrderForConcludeEvent.name);
            LocalTx localTx2 = new LocalTx(CancelDecreaseActualStorageForConcludeEvent.name, CancelDecreaseActualStorageForConcludeEvent.name);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            localTxes.add(localTx2);
            DistributedTx dtx = DistributedTx.cancelOf(event.getDistributedTx(), localTxes);
            dtx.startAllLocalTx();
            DomainRegistry.getDistributedTxRepository().store(dtx);
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get(DTX_COMMAND), CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForConcludeEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelDecreaseActualStorageForConcludeEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
            return null;
        }, CREATE_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void confirmPaymentFailed(DistributedTxFailedEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {

            LocalTx localTx1 = new LocalTx(CancelUpdateOrderPaymentEvent.name, CancelUpdateOrderPaymentEvent.name);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            DistributedTx dtx = DistributedTx.cancelOf(deserialize.getDistributedTx(), localTxes);
            dtx.startAllLocalTx();
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get(DTX_COMMAND), CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CancelUpdateOrderPaymentEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
            DomainRegistry.getDistributedTxRepository().store(dtx);
            return null;
        }, CREATE_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void createOrderFailed(DistributedTxFailedEvent event) {
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
        }, CREATE_DTX);
    }

    @SubscribeForEvent
    @Transactional
    public void createCreateOrderTask(CreateCreateOrderDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            CommonOrderCommand command = event.getCommand();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                LocalTx localTx1 = new LocalTx(GeneratePaymentQRLinkEvent.name, GeneratePaymentQRLinkEvent.name);
                LocalTx localTx2 = new LocalTx(DecreaseOrderStorageForCreateEvent.name, DecreaseOrderStorageForCreateEvent.name);
                LocalTx localTx3 = new LocalTx(ClearCartEvent.name, ClearCartEvent.name);
                LocalTx localTx4 = new LocalTx(SaveNewOrderEvent.name, SaveNewOrderEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                localTxes.add(localTx2);
                localTxes.add(localTx3);
                localTxes.add(localTx4);
                DistributedTx distributedTx = new DistributedTx(localTxes, "createOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId(), AppConstant.CREATE_ORDER_DTX_FAILED_EVENT);
                distributedTx.startLocalTx(GeneratePaymentQRLinkEvent.name);
                distributedTx.startLocalTx(DecreaseOrderStorageForCreateEvent.name);
                distributedTx.startLocalTx(ClearCartEvent.name);
                distributedTx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                DomainEventPublisher.instance().publish(new GeneratePaymentQRLinkEvent(event.getCommand().getOrderId(), event.getCommand().getTxId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new DecreaseOrderStorageForCreateEvent(event.getCommand().getOrderId(), event.getCommand().getTxId(), distributedTx.getId(), command));
                DomainEventPublisher.instance().publish(new ClearCartEvent(command, event.getCommand().getTxId(), distributedTx.getId()));
                DomainRegistry.getDistributedTxRepository().store(distributedTx);
            }, command.getOrderId());
            return null;
        }, CREATE_DTX);
    }


    @Transactional
    @SubscribeForEvent
    public void invalidOrderFailed(DistributedTxFailedEvent event) {
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
        }, CREATE_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void recycleOrderFailed(DistributedTxFailedEvent event) {
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
        }, CREATE_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void reserveOrderFailed(DistributedTxFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {

            LocalTx localTx1 = new LocalTx(CancelDecreaseOrderStorageForReserveEvent.name, CancelDecreaseOrderStorageForReserveEvent.name);
            LocalTx localTx2 = new LocalTx(CancelUpdateOrderForReserveEvent.name, CancelUpdateOrderForReserveEvent.name);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            localTxes.add(localTx2);
            DistributedTx dtx = DistributedTx.cancelOf(event.getDistributedTx(), localTxes);
            dtx.startAllLocalTx();
            DomainRegistry.getDistributedTxRepository().store(dtx);
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get(DTX_COMMAND), CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CancelDecreaseOrderStorageForReserveEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForReserveEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));

            return null;
        }, CREATE_DTX);

    }


    @Transactional
    @SubscribeForEvent
    public void updateAddressFailed(DistributedTxFailedEvent event) {
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
        }, CREATE_DTX);
    }

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
                DistributedTx distributedTx = new DistributedTx(localTxes, "ConcludeOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId(), AppConstant.CONCLUDE_ORDER_DTX_FAILED_EVENT);
                distributedTx.startLocalTx(UpdateOrderForConcludeEvent.name);
                distributedTx.startLocalTx(DecreaseActualStorageForConcludeEvent.name);
                distributedTx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new UpdateOrderForConcludeEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainEventPublisher.instance().publish(new DecreaseActualStorageForConcludeEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainRegistry.getDistributedTxRepository().store(distributedTx);

            }, orderId);
            return null;
        }, CREATE_DTX);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(CreateConfirmOrderPaymentDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {

                LocalTx localTx1 = new LocalTx(UpdateOrderPaymentSuccessEvent.name, UpdateOrderPaymentSuccessEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                DistributedTx distributedTx = new DistributedTx(localTxes, "ConfirmOrderPaymentOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId(), AppConstant.CONFIRM_ORDER_PAYMENT_FAILED_EVENT);
                distributedTx.startLocalTx(UpdateOrderPaymentSuccessEvent.name);
                distributedTx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();

                DomainEventPublisher.instance().publish(new UpdateOrderPaymentSuccessEvent(command, distributedTx.getLockId(), distributedTx.getChangeId(), distributedTx.getId()));
                DomainRegistry.getDistributedTxRepository().store(distributedTx);
            }, orderId);

            return null;
        }, CREATE_DTX);
    }

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
        }, CREATE_DTX);
    }

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
                DistributedTx dtx = new DistributedTx(localTxes, "RecycleOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId(), AppConstant.RECYCLE_ORDER_DTX_FAILED_EVENT);
                dtx.startAllLocalTx();
                dtx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new IncreaseOrderStorageForRecycleEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainEventPublisher.instance().publish(new UpdateOrderForRecycleEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainRegistry.getDistributedTxRepository().store(dtx);


            }, orderId);
            return null;
        }, CREATE_DTX);
    }

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
                DistributedTx dtx = new DistributedTx(localTxes, "ReserveOrderDtx", event.getCommand().getTxId(), event.getCommand().getOrderId(), AppConstant.RESERVE_ORDER_DTX_FAILED_EVENT);
                dtx.startAllLocalTx();
                dtx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new DecreaseOrderStorageForReserveEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainEventPublisher.instance().publish(new UpdateOrderForReserveEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainRegistry.getDistributedTxRepository().store(dtx);

            }, orderId);
            return null;
        }, CREATE_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(CreateUpdateOrderAddressDTXEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            String orderId = event.getCommand().getOrderId();
            DomainRegistry.getIsolationService().hasNoActiveDtx((ignored) -> {
                log.debug("start of creating {}", event.getName());
                LocalTx localTx1 = new LocalTx(UpdateOrderForUpdateOrderAddressEvent.name, UpdateOrderForUpdateOrderAddressEvent.name);
                Set<LocalTx> localTxes = new HashSet<>();
                localTxes.add(localTx1);
                DistributedTx dtx = new DistributedTx(localTxes, "UpdateOrderAddressDtx", event.getCommand().getTxId(), event.getCommand().getOrderId(), AppConstant.UPDATE_ORDER_ADDRESS_DTX_FAILED_EVENT);
                dtx.startAllLocalTx();
                dtx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new UpdateOrderForUpdateOrderAddressEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainRegistry.getDistributedTxRepository().store(dtx);

            }, orderId);
            return null;
        }, CREATE_DTX);
    }


    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(GeneratePaymentQRLinkReplyCommand command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handleReply(GeneratePaymentQRLinkEvent.name, command);
                SaveNewOrderEvent event = new SaveNewOrderEvent(command,
                        CommonDomainRegistry.getCustomObjectSerializer().deserialize(e.getParameters().get(DTX_COMMAND), CommonOrderCommand.class), e.getId(),
                        e.getChangeId());
                e.startLocalTx(SaveNewOrderEvent.name);
                DomainEventPublisher.instance().publish(event);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CREATE_DTX);
    }

    @Transactional
    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    public void handle(ReplyEvent command, String name) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handleReply(name, command);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CREATE_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(dtxId);
            byId.ifPresent(DistributedTx::cancel);
            return null;
        }, CANCEL_DTX);
    }

    public SumPagedRep<DistributedTx> query(String queryParam, String pageParam, String skipCount) {
        DistributedTxQuery var0 = new DistributedTxQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getDistributedTxRepository().query(var0);
    }

    public Optional<DistributedTx> query(long id) {
        return DomainRegistry.getDistributedTxRepository().getById(id);
    }

    /**
     * retry start ltx
     */
    @Transactional
    @SubscribeForEvent
    public void handle(DistributedTxSuccessEvent deserialize) {
        DistributedTx.retryStartedLtx(deserialize);
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
        }, RESOLVE_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(LocalTxFailedEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(deserialize.getTaskId()), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(deserialize.getTaskId());
            byId.ifPresent(DistributedTx::cancel);
            return null;
        }, CANCEL_DTX);
    }

}
