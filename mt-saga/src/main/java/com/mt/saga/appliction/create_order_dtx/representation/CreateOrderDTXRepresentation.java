package com.mt.saga.appliction.create_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.event.ClearCartEvent;
import com.mt.saga.domain.model.create_order_dtx.event.DecreaseOrderStorageForCreateEvent;
import com.mt.saga.domain.model.create_order_dtx.event.GeneratePaymentQRLinkEvent;
import com.mt.saga.domain.model.create_order_dtx.event.SaveNewOrderEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

import static com.mt.saga.appliction.create_order_dtx.CreateOrderDTXApplicationService.*;

@Setter
@Getter
public class CreateOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String SAVE_NEW_ORDER_LTX = "SAVE_NEW_ORDER_LTX";
    private static final String DECREASE_ORDER_STORAGE_LTX = "DECREASE_ORDER_STORAGE_LTX";
    private static final String GENERATE_PAYMENT_LINK = "GENERATE_PAYMENT_LINK_LTX";
    private static final String REMOVE_ITEMS_FROM_CART = "REMOVE_ITEMS_FROM_CART_LTX";

    public CreateOrderDTXRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(SAVE_NEW_ORDER_LTX, var0.getLocalTxStatusByName(APP_SAVE_NEW_ORDER));
        statusMap.put(DECREASE_ORDER_STORAGE_LTX, var0.getLocalTxStatusByName(APP_DECREASE_ORDER_STORAGE));
        statusMap.put(GENERATE_PAYMENT_LINK, var0.getLocalTxStatusByName(APP_GENERATE_PAYMENT_QR_LINK));
        statusMap.put(REMOVE_ITEMS_FROM_CART, var0.getLocalTxStatusByName(APP_CLEAR_CART));
        emptyOptMap.put(SAVE_NEW_ORDER_LTX, var0.isLocalTxEmptyOptByName(APP_SAVE_NEW_ORDER));
        emptyOptMap.put(DECREASE_ORDER_STORAGE_LTX, var0.isLocalTxEmptyOptByName(APP_DECREASE_ORDER_STORAGE));
        emptyOptMap.put(GENERATE_PAYMENT_LINK, var0.isLocalTxEmptyOptByName(APP_GENERATE_PAYMENT_QR_LINK));
        emptyOptMap.put(REMOVE_ITEMS_FROM_CART, var0.isLocalTxEmptyOptByName(APP_CLEAR_CART));
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(GeneratePaymentQRLinkEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(GENERATE_PAYMENT_LINK,e.getId());
            }
            else if(DecreaseOrderStorageForCreateEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(DECREASE_ORDER_STORAGE_LTX,e.getId());
            }
            else if(SaveNewOrderEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(SAVE_NEW_ORDER_LTX,e.getId());
            }
            else if(ClearCartEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(REMOVE_ITEMS_FROM_CART,e.getId());
            }
        });


    }
}
