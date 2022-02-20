package com.mt.saga.appliction.reserve_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.clazz.ClassUtility;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXCardRepresentation;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.domain.model.reserve_order_dtx.event.DecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.UpdateOrderForReserveEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReserveOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String UPDATE_ORDER_LTX = "UPDATE_ORDER_LTX";
    private static final String DECREASE_ORDER_STORAGE_LTX = "DECREASE_ORDER_STORAGE_LTX";

    public ReserveOrderDTXRepresentation(ReserveOrderDTX var0 ) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(UPDATE_ORDER_LTX, var0.getUpdateOrderLTXStatus());
        statusMap.put(DECREASE_ORDER_STORAGE_LTX, var0.getDecreaseOrderStorageLTXStatus());
        emptyOptMap.put(UPDATE_ORDER_LTX, var0.isUpdateOrderLTXEmptyOpt());
        emptyOptMap.put(DECREASE_ORDER_STORAGE_LTX, var0.isDecreaseOrderStorageLTXEmptyOpt());
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(DecreaseOrderStorageForReserveEvent.name.equalsIgnoreCase(ClassUtility.getShortName(e.getName()))){
                idMap.put(DECREASE_ORDER_STORAGE_LTX,e.getId());
            }
            else if(UpdateOrderForReserveEvent.name.equalsIgnoreCase(ClassUtility.getShortName(e.getName()))){
                idMap.put(UPDATE_ORDER_LTX,e.getId());
            }
        });

    }
}

