package com.mt.saga.appliction.invalid_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.invalid_order.event.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvalidOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String REMOVE_ORDER_LTX = "REMOVE_ORDER_LTX";
    private static final String INCREASE_STORAGE_LTX = "INCREASE_STORAGE_LTX";
    private static final String REMOVE_PAYMENT_LINK_LTX = "REMOVE_PAYMENT_LINK_LTX";
    private static final String RESTORE_CART_LTX = "RESTORE_CART_LTX";

    public InvalidOrderDTXRepresentation(InvalidOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(REMOVE_ORDER_LTX, var0.getRemoveOrderLTXStatus());
        statusMap.put(INCREASE_STORAGE_LTX, var0.getIncreaseStorageLTXStatus());
        statusMap.put(REMOVE_PAYMENT_LINK_LTX, var0.getRemovePaymentLinkLTXStatus());
        statusMap.put(RESTORE_CART_LTX, var0.getRestoreCartLTXStatus());

        emptyOptMap.put(REMOVE_ORDER_LTX, var0.isRemoveOrderLTXEmptyOpt());
        emptyOptMap.put(INCREASE_STORAGE_LTX, var0.isIncreaseStorageLTXEmptyOpt());
        emptyOptMap.put(REMOVE_PAYMENT_LINK_LTX, var0.isRemovePaymentLinkLTXEmptyOpt());
        emptyOptMap.put(RESTORE_CART_LTX, var0.isRestoreCartLTXEmptyOpt());

        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(RemoveOrderForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(REMOVE_ORDER_LTX,e.getId());
            }
            else if(IncreaseStorageForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(INCREASE_STORAGE_LTX,e.getId());
            }
            else if(RemovePaymentQRLinkForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(REMOVE_PAYMENT_LINK_LTX,e.getId());
            }
            else if(RestoreCartForInvalidEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(RESTORE_CART_LTX,e.getId());
            }
        });

    }
}
