package com.mt.saga.appliction.invalid_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.invalid_order.event.IncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.invalid_order.event.RestoreCartForInvalidEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvalidOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String REMOVE_ORDER_LTX = "REMOVE_ORDER_LTX";
    private static final String INCREASE_STORAGE_LTX = "INCREASE_STORAGE_LTX";
    private static final String REMOVE_PAYMENT_LINK_LTX = "REMOVE_PAYMENT_LINK_LTX";
    private static final String RESTORE_CART_LTX = "RESTORE_CART_LTX";

    public InvalidOrderDTXRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(REMOVE_ORDER_LTX, var0.getLocalTxStatusByName(RemoveOrderForInvalidEvent.name));
        statusMap.put(INCREASE_STORAGE_LTX, var0.getLocalTxStatusByName(IncreaseStorageForInvalidEvent.name));
        statusMap.put(REMOVE_PAYMENT_LINK_LTX, var0.getLocalTxStatusByName(RemovePaymentQRLinkForInvalidEvent.name));
        statusMap.put(RESTORE_CART_LTX, var0.getLocalTxStatusByName(RestoreCartForInvalidEvent.name));

        emptyOptMap.put(REMOVE_ORDER_LTX, var0.isLocalTxEmptyOptByName(RemoveOrderForInvalidEvent.name));
        emptyOptMap.put(INCREASE_STORAGE_LTX, var0.isLocalTxEmptyOptByName(IncreaseStorageForInvalidEvent.name));
        emptyOptMap.put(REMOVE_PAYMENT_LINK_LTX, var0.isLocalTxEmptyOptByName(RemovePaymentQRLinkForInvalidEvent.name));
        emptyOptMap.put(RESTORE_CART_LTX, var0.isLocalTxEmptyOptByName(RestoreCartForInvalidEvent.name));

        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e -> {
            if (RemoveOrderForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(REMOVE_ORDER_LTX, e.getId());
            } else if (IncreaseStorageForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(INCREASE_STORAGE_LTX, e.getId());
            } else if (RemovePaymentQRLinkForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(REMOVE_PAYMENT_LINK_LTX, e.getId());
            } else if (RestoreCartForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(RESTORE_CART_LTX, e.getId());
            }
        });

    }
}
