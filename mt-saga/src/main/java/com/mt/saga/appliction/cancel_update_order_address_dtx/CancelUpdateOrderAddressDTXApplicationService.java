package com.mt.saga.appliction.cancel_update_order_address_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxFailedEvent;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService.DTX_COMMAND;

@Service
public class CancelUpdateOrderAddressDTXApplicationService {
    private static final String CANCEL_UPDATE_ORDER_ADDRESS_DTX = "CancelUpdateOrderAddressDTX";

    @Transactional
    @SubscribeForEvent
    public void handle(DistributedTxFailedEvent event) {
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

}
