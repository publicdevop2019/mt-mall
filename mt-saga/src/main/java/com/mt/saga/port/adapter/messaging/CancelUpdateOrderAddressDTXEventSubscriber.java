package com.mt.saga.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderForUpdateOrderAddressEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTxFailedEvent;
import com.mt.saga.domain.model.distributed_tx.ReplyEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelUpdateOrderAddressDTXEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${mt.app.name.mt2}")
    private String profile;

    @EventListener(ApplicationReadyEvent.class)
    private void listener() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, AppConstant.UPDATE_ORDER_ADDRESS_DTX_FAILED_EVENT, (event) -> {
            DistributedTxFailedEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), DistributedTxFailedEvent.class);
            ApplicationServiceRegistry.getCancelUpdateOrderAddressDTXApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService().replyCancelOf(profile, false, AppConstant.UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT, (event) -> {
            ReplyEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ReplyEvent.class);
            ApplicationServiceRegistry.getDistributedTxApplicationService().handle(deserialize, CancelUpdateOrderForUpdateOrderAddressEvent.name);
        });
    }
}
