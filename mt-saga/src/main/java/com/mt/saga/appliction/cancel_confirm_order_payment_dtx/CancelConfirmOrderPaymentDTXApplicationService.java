package com.mt.saga.appliction.cancel_confirm_order_payment_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command.CancelUpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelUpdateOrderPaymentEvent;
import com.mt.saga.domain.model.distributed_tx.DTXFailedEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.domain.model.distributed_tx.LocalTx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mt.saga.appliction.create_order_dtx.CreateOrderDTXApplicationService.DTX_COMMAND;

@Service
@Slf4j

public class CancelConfirmOrderPaymentDTXApplicationService {

    private static final String CANCEL_CONFIRM_ORDER_PAYMENT_DTX = "CancelConfirmOrderPaymentDTX";
    private static final String CANCEL_CONFIRM_ORDER_PAYMENT_DTX_RESOLVED = "CancelConfirmOrderPaymentDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(DTXFailedEvent deserialize) {
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
    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderPaymentSuccessReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(),(change)->{
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(deserialize.getTaskId());
            byId.ifPresent(e -> {
                e.handle(CancelUpdateOrderPaymentEvent.name, deserialize);
                DomainRegistry.getDistributedTxRepository().store(e);
            });
            return null;
        }, CANCEL_CONFIRM_ORDER_PAYMENT_DTX);
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
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id),(change)->{
            Optional<DistributedTx> byId = DomainRegistry.getDistributedTxRepository().getById(id);
            byId.ifPresent(e -> {
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_CONFIRM_ORDER_PAYMENT_DTX_RESOLVED);
    }
}
