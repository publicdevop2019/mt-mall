package com.mt.saga.appliction.update_order_address_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import com.mt.saga.domain.model.order_state_machine.event.CreateUpdateOrderAddressDTXEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Service
@Slf4j
public class UpdateOrderAddressDTXApplicationService {

    private static final String UPDATE_ORDER_ADDRESS_DTX = "UpdateOrderAddressDTX";
    private static final String CANCEL_UPDATE_ORDER_ADDRESS_DTX = "CancelDtx";

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
        }, UPDATE_ORDER_ADDRESS_DTX);
    }

}
