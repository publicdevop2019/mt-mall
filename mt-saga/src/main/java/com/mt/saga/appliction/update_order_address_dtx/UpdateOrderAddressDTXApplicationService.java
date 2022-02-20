package com.mt.saga.appliction.update_order_address_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.DTXDistLock;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.update_order_address_dtx.command.OrderUpdateForAddressUpdateFailedCommand;
import com.mt.saga.appliction.update_order_address_dtx.command.UpdateOrderAddressSuccessReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderAddressDTXSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateUpdateOrderAddressDTXEvent;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTXQuery;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderForUpdateOrderAddressEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

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
                UpdateOrderAddressDTX dtx = new UpdateOrderAddressDTX(event);
                DomainEventPublisher.instance().publish(new UpdateOrderForUpdateOrderAddressEvent(dtx));
                dtx.markAsStarted();
                DomainRegistry.getUpdateOrderAddressDTXRepository().createOrUpdate(dtx);
            }, orderId);
            return null;
        }, UPDATE_ORDER_ADDRESS_DTX);
    }

    @DTXDistLock(keyExpression = "#p0.taskId")
    @SubscribeForEvent
    @Transactional
    public void handle(UpdateOrderAddressSuccessReplyEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (change) -> {
            Optional<UpdateOrderAddressDTX> byId = DomainRegistry.getUpdateOrderAddressDTXRepository().getById(event.getTaskId());
            byId.ifPresent(e -> e.updateStatus(event));
            return null;
        }, UPDATE_ORDER_ADDRESS_DTX);
    }

    @Transactional
    @SubscribeForEvent
    public void cancel(long dtxId) {
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(String.valueOf(dtxId), (change) -> {
                    Optional<UpdateOrderAddressDTX> byId = DomainRegistry.getUpdateOrderAddressDTXRepository().getById(dtxId);
                    byId.ifPresent(UpdateOrderAddressDTX::cancel);
                    return null;
                },
                CANCEL_UPDATE_ORDER_ADDRESS_DTX);
    }

    public SumPagedRep<UpdateOrderAddressDTX> query(String queryParam, String pageParam, String skipCount) {
        UpdateOrderAddressDTXQuery var0 = new UpdateOrderAddressDTXQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getUpdateOrderAddressDTXRepository().query(var0);
    }

    public Optional<UpdateOrderAddressDTX> query(long id) {
        return DomainRegistry.getUpdateOrderAddressDTXRepository().getById(id);
    }

    public void handle(CancelUpdateOrderAddressDTXSuccessEvent deserialize) {
        Optional<UpdateOrderAddressDTX> byId = DomainRegistry.getUpdateOrderAddressDTXRepository().getById(Long.parseLong(deserialize.getDtxId()));
        byId.ifPresent(UpdateOrderAddressDTX::retryStartedLtx);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(OrderUpdateForAddressUpdateFailedCommand deserialize) {
        cancel(deserialize.getTaskId());
    }
}
