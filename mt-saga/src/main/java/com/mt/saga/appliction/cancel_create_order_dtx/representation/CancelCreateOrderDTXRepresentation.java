package com.mt.saga.appliction.cancel_create_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.clazz.ClassUtility;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelClearCartEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelDecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelGeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelSaveNewOrderEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelCreateOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_DECREASE_ORDER_STORAGE = "CANCEL_DECREASE_ORDER_STORAGE_LTX";
    private static final String CANCEL_GENERATE_PAYMENT_LINK = "CANCEL_GENERATE_PAYMENT_LINK_LTX";
    private static final String CANCEL_SAVE_NEW_ORDER = "CANCEL_SAVE_NEW_ORDER_LTX";
    private static final String CANCEL_REMOVE_ITEMS_FROM_CART = "CANCEL_REMOVE_ITEMS_FROM_CART_LTX";

    public CancelCreateOrderDTXRepresentation(CancelCreateOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_DECREASE_ORDER_STORAGE, var0.getCancelDecreaseOrderStorageLTXStatus());
        statusMap.put(CANCEL_GENERATE_PAYMENT_LINK, var0.getCancelGeneratePaymentLinkLTXStatus());
        statusMap.put(CANCEL_SAVE_NEW_ORDER, var0.getCancelSaveNewOrderLTXStatus());
        statusMap.put(CANCEL_REMOVE_ITEMS_FROM_CART, var0.getCancelRemoveItemsFromCartLTXStatus());
        emptyOptMap.put(CANCEL_DECREASE_ORDER_STORAGE, var0.isCancelDecreaseOrderStorageLTXEmptyOpt());
        emptyOptMap.put(CANCEL_GENERATE_PAYMENT_LINK, var0.isCancelGeneratePaymentLinkLTXEmptyOpt());
        emptyOptMap.put(CANCEL_SAVE_NEW_ORDER, var0.isCancelSaveNewOrderLTXEmptyOpt());
        emptyOptMap.put(CANCEL_REMOVE_ITEMS_FROM_CART, var0.isCancelRemoveItemsFromCartLTXEmptyOpt());
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(CancelClearCartEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_REMOVE_ITEMS_FROM_CART,e.getId());
            }
            else if(CancelDecreaseOrderStorageForCreateEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_DECREASE_ORDER_STORAGE,e.getId());
            }
            else if(CancelGeneratePaymentQRLinkEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_GENERATE_PAYMENT_LINK,e.getId());
            }
            else if(CancelSaveNewOrderEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_SAVE_NEW_ORDER,e.getId());
            }
        });

    }
}

