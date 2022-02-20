package com.mt.saga.appliction.cancel_update_order_address_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.cancel_update_order_address_dtx.command.CancelUpdateOrderAddressReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTXQuery;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderAddressDTXFailedEvent;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CancelUpdateOrderAddressDTXApplicationService {
    private static final String CANCEL_UPDATE_ORDER_ADDRESS_DTX = "CancelUpdateOrderAddressDTX";
    private static final String CANCEL_UPDATE_ORDER_ADDRESS_DTX_RESOLVED = "CancelUpdateOrderAddressDTXResolved";

    @Transactional
    @SubscribeForEvent
    public void handle(UpdateOrderAddressDTXFailedEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            CancelUpdateOrderAddressDTX dtx = new CancelUpdateOrderAddressDTX(deserialize);
            DomainRegistry.getCancelUpdateOrderAddressDTXRepository().createOrUpdate(dtx);
            DomainEventPublisher.instance().publish(new CancelUpdateOrderForUpdateOrderAddressEvent(dtx));
            dtx.markAsStarted();
            DomainRegistry.getCancelUpdateOrderAddressDTXRepository().createOrUpdate(dtx);
            return null;
        }, CANCEL_UPDATE_ORDER_ADDRESS_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @Transactional
    @SubscribeForEvent
    public void handle(CancelUpdateOrderAddressReplyEvent deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(deserialize.getId().toString(), (change) -> {
            Optional<CancelUpdateOrderAddressDTX> byId = DomainRegistry.getCancelUpdateOrderAddressDTXRepository().getById(deserialize.getTaskId());
            byId.ifPresent(e -> {
                e.updateStatus(deserialize);
            });
            return null;
        }, CANCEL_UPDATE_ORDER_ADDRESS_DTX);
    }

    public SumPagedRep<CancelUpdateOrderAddressDTX> query(String queryParam, String pageParam, String skipCount) {
        CancelUpdateOrderAddressDTXQuery var0 = new CancelUpdateOrderAddressDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getCancelUpdateOrderAddressDTXRepository().query(var0);
    }

    public Optional<CancelUpdateOrderAddressDTX> query(long id) {
        return DomainRegistry.getCancelUpdateOrderAddressDTXRepository().getById(id);
    }

    @DTXDistLock(keyExpression = "#p0")
    @Transactional
    public void resolve(long id, ResolveReason resolveReason) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(id), (change) -> {
            Optional<CancelUpdateOrderAddressDTX> byId = DomainRegistry.getCancelUpdateOrderAddressDTXRepository().getById(id);
            byId.ifPresent(e -> {
                e.markAsResolved(resolveReason);
            });
            return null;
        }, CANCEL_UPDATE_ORDER_ADDRESS_DTX_RESOLVED);
    }
}
