package com.mt.saga.appliction.update_order_address_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.appliction.update_order_address_dtx.command.OrderUpdateForAddressUpdateFailedCommand;
import com.mt.saga.appliction.update_order_address_dtx.command.UpdateOrderAddressSuccessReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.DTXSuccessEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.order_state_machine.event.CreateUpdateOrderAddressDTXEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mt.saga.appliction.create_order_dtx.CreateOrderDTXApplicationService.DTX_COMMAND;

@Service
@Slf4j
public class UpdateOrderAddressDTXApplicationService {

    private static final String UPDATE_ORDER_ADDRESS_DTX = "UpdateOrderAddressDTX";
    private static final String CANCEL_UPDATE_ORDER_ADDRESS_DTX = "SystemCancelUpdateOrderAddressDtx";

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
                DistributedTx dtx = new DistributedTx(localTxes, "UpdateOrderAddressDtx", event.getCommand().getTxId(), event.getCommand().getOrderId());
                dtx.startAllLocalTx();
                dtx.updateParams(DTX_COMMAND, CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand()));
                CommonOrderCommand command = event.getCommand();
                DomainEventPublisher.instance().publish(new UpdateOrderForUpdateOrderAddressEvent(command, dtx.getLockId(), dtx.getChangeId(), dtx.getId()));
                DomainRegistry.getDistributedTxRepository().store(dtx);

            }, orderId);
            return null;
        }, UPDATE_ORDER_ADDRESS_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    @Transactional
    public void handle(UpdateOrderAddressSuccessReplyEvent command) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(command.getId().toString(), (change) -> {
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(command.getTaskId());
            byId.ifPresent(e -> {
                e.handle(UpdateOrderForUpdateOrderAddressEvent.name, command);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, UPDATE_ORDER_ADDRESS_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
                    Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(dtxId);
                    byId.ifPresent(e -> e.cancel(AppConstant.UPDATE_ORDER_ADDRESS_DTX_FAILED_EVENT));
                    return null;
                },
                CANCEL_UPDATE_ORDER_ADDRESS_DTX);
    }


    public SumPagedRep<DistributedTx> query(String queryParam, String pageParam, String skipCount) {
        DistributedTxQuery var0 = new DistributedTxQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getDistributedTxRepository().query(var0);
    }

    public Optional<DistributedTx> query(long id) {
        return DomainRegistry.getDistributedTxRepository().getById(id);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(DTXSuccessEvent deserialize) {
        DistributedTx.handle(deserialize);
    }

    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForAddressUpdateFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
