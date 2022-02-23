package com.mt.saga.appliction.cancel_reserve_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelDecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelUpdateOrderForReserveEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelReserveOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_DECREASE_ORDER_STORAGE_LTX = "CANCEL_DECREASE_ORDER_STORAGE_LTX";
    private static final String CANCEL_UPDATE_ORDER_LTX = "CANCEL_UPDATE_ORDER_LTX";

    public CancelReserveOrderDTXRepresentation(CancelReserveOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_DECREASE_ORDER_STORAGE_LTX, var0.getCancelDecreaseOrderStorageLTXStatus());
        statusMap.put(CANCEL_UPDATE_ORDER_LTX, var0.getCancelUpdateOrderLTXStatus());
        emptyOptMap.put(CANCEL_DECREASE_ORDER_STORAGE_LTX, var0.isCancelDecreaseOrderStorageLTXEmptyOpt());
        emptyOptMap.put(CANCEL_UPDATE_ORDER_LTX, var0.isCancelUpdateOrderLTXEmptyOpt());
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(CancelDecreaseOrderStorageForReserveEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_DECREASE_ORDER_STORAGE_LTX,e.getId());
            }
            else if(CancelUpdateOrderForReserveEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_UPDATE_ORDER_LTX,e.getId());
            }
        });

    }
}

