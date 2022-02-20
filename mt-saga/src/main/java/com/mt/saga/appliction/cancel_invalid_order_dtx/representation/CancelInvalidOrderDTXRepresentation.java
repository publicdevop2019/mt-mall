package com.mt.saga.appliction.cancel_invalid_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import com.mt.saga.domain.model.cancel_invalid_order.event.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelInvalidOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_REMOVE_ORDER_LTX = "CANCEL_REMOVE_ORDER_LTX";
    private static final String CANCEL_INCREASE_STORAGE_LTX = "CANCEL_INCREASE_STORAGE_LTX";
    private static final String CANCEL_REMOVE_PAYMENT_LINK_LTX = "CANCEL_REMOVE_PAYMENT_LINK_LTX";
    private static final String CANCEL_RESTORE_CART_LTX = "CANCEL_RESTORE_CART_LTX";

    public CancelInvalidOrderDTXRepresentation(CancelInvalidOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_REMOVE_ORDER_LTX, var0.getCancelRemoveOrderLTXStatus());
        statusMap.put(CANCEL_INCREASE_STORAGE_LTX, var0.getCancelIncreaseStorageLTXStatus());
        statusMap.put(CANCEL_REMOVE_PAYMENT_LINK_LTX, var0.getCancelRemovePaymentLinkLTXStatus());
        statusMap.put(CANCEL_RESTORE_CART_LTX, var0.getCancelRestoreCartLTXStatus());

        emptyOptMap.put(CANCEL_REMOVE_ORDER_LTX, var0.isCancelRemoveOrderLTXEmptyOpt());
        emptyOptMap.put(CANCEL_INCREASE_STORAGE_LTX, var0.isCancelIncreaseStorageLTXEmptyOpt());
        emptyOptMap.put(CANCEL_REMOVE_PAYMENT_LINK_LTX, var0.isCancelRemovePaymentLinkLTXEmptyOpt());
        emptyOptMap.put(CANCEL_RESTORE_CART_LTX, var0.isCancelRestoreCartLTXEmptyOpt());

        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(CancelRemoveOrderForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_REMOVE_ORDER_LTX,e.getId());
            }
            else if(CancelIncreaseStorageForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_INCREASE_STORAGE_LTX,e.getId());
            }
            else if(CancelRemovePaymentQRLinkForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_REMOVE_PAYMENT_LINK_LTX,e.getId());
            }
            else if(CancelRestoreCartForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_RESTORE_CART_LTX,e.getId());
            }
        });

    }
}
