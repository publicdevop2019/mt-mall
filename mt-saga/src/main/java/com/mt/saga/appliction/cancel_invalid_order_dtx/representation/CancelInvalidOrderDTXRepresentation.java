package com.mt.saga.appliction.cancel_invalid_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelIncreaseStorageForInvalidEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelRemoveOrderForInvalidEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelRemovePaymentQRLinkForInvalidEvent;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelRestoreCartForInvalidEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelInvalidOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_REMOVE_ORDER_LTX = "CANCEL_REMOVE_ORDER_LTX";
    private static final String CANCEL_INCREASE_STORAGE_LTX = "CANCEL_INCREASE_STORAGE_LTX";
    private static final String CANCEL_REMOVE_PAYMENT_LINK_LTX = "CANCEL_REMOVE_PAYMENT_LINK_LTX";
    private static final String CANCEL_RESTORE_CART_LTX = "CANCEL_RESTORE_CART_LTX";

    public CancelInvalidOrderDTXRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_REMOVE_ORDER_LTX, var0.getLocalTxStatusByName(CancelRemoveOrderForInvalidEvent.name));
        statusMap.put(CANCEL_INCREASE_STORAGE_LTX, var0.getLocalTxStatusByName(CancelIncreaseStorageForInvalidEvent.name));
        statusMap.put(CANCEL_REMOVE_PAYMENT_LINK_LTX, var0.getLocalTxStatusByName(CancelRemovePaymentQRLinkForInvalidEvent.name));
        statusMap.put(CANCEL_RESTORE_CART_LTX, var0.getLocalTxStatusByName(CancelRestoreCartForInvalidEvent.name));

        emptyOptMap.put(CANCEL_REMOVE_ORDER_LTX, var0.isLocalTxEmptyOptByName(CancelRemoveOrderForInvalidEvent.name));
        emptyOptMap.put(CANCEL_INCREASE_STORAGE_LTX, var0.isLocalTxEmptyOptByName(CancelIncreaseStorageForInvalidEvent.name));
        emptyOptMap.put(CANCEL_REMOVE_PAYMENT_LINK_LTX, var0.isLocalTxEmptyOptByName(CancelRemovePaymentQRLinkForInvalidEvent.name));
        emptyOptMap.put(CANCEL_RESTORE_CART_LTX, var0.isLocalTxEmptyOptByName(CancelRestoreCartForInvalidEvent.name));

        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e -> {
            if (CancelRemoveOrderForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(CANCEL_REMOVE_ORDER_LTX, e.getId());
            } else if (CancelIncreaseStorageForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(CANCEL_INCREASE_STORAGE_LTX, e.getId());
            } else if (CancelRemovePaymentQRLinkForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(CANCEL_REMOVE_PAYMENT_LINK_LTX, e.getId());
            } else if (CancelRestoreCartForInvalidEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(CANCEL_RESTORE_CART_LTX, e.getId());
            }
        });

    }
}
