package com.mt.saga.appliction.cancel_confirm_order_payment_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelUpdateOrderPaymentEvent;
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

public class CancelConfirmOrderPaymentDTXApplicationService {

    private static final String CANCEL_CONFIRM_ORDER_PAYMENT_DTX = "CancelConfirmOrderPaymentDTX";

    @Transactional
    @SubscribeForEvent
    public void handle(DistributedTxFailedEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{

            LocalTx localTx1 = new LocalTx(CancelUpdateOrderPaymentEvent.name, CancelUpdateOrderPaymentEvent.name);
            Set<LocalTx> localTxes = new HashSet<>();
            localTxes.add(localTx1);
            DistributedTx dtx = DistributedTx.cancelOf(deserialize.getDistributedTx(), localTxes);
            dtx.startAllLocalTx();
            CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getParameters().get(DTX_COMMAND), CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CancelUpdateOrderPaymentEvent(command,dtx.getLockId(),dtx.getChangeId(), dtx.getId()));
            DomainRegistry.getDistributedTxRepository().store(dtx);
            return null;
        },CANCEL_CONFIRM_ORDER_PAYMENT_DTX);
    }

}
