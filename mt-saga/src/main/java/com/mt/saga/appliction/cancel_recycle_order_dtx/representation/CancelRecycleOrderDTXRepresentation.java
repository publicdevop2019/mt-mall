package com.mt.saga.appliction.cancel_recycle_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.clazz.ClassUtility;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelIncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelUpdateOrderForRecycleEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelRecycleOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_INCREASE_ORDER_STORAGE_LTX = "CANCEL_INCREASE_ORDER_STORAGE_LTX";
    private static final String CANCEL_UPDATE_ORDER_LTX = "CANCEL_UPDATE_ORDER_LTX";

    public CancelRecycleOrderDTXRepresentation(CancelRecycleOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_INCREASE_ORDER_STORAGE_LTX, var0.getCancelIncreaseOrderStorageLTXStatus());
        statusMap.put(CANCEL_UPDATE_ORDER_LTX, var0.getCancelUpdateOrderLTXStatus());
        emptyOptMap.put(CANCEL_INCREASE_ORDER_STORAGE_LTX, var0.isCancelIncreaseOrderStorageLTXEmptyOpt());
        emptyOptMap.put(CANCEL_UPDATE_ORDER_LTX, var0.isCancelUpdateOrderLTXEmptyOpt());
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(CancelIncreaseOrderStorageForRecycleEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_INCREASE_ORDER_STORAGE_LTX,e.getId());
            }
            else if(CancelUpdateOrderForRecycleEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_UPDATE_ORDER_LTX,e.getId());
            }
        });

    }
}

