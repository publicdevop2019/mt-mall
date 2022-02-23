package com.mt.saga.appliction.conclude_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.domain.model.conclude_order_dtx.event.DecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.UpdateOrderForConcludeEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConcludeOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String DECREASE_ACTUAL_STORAGE_LTX = "DECREASE_ACTUAL_STORAGE_LTX";
    private static final String UPDATE_ORDER_LTX = "UPDATE_ORDER_LTX";

    public ConcludeOrderDTXRepresentation(ConcludeOrderDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(DECREASE_ACTUAL_STORAGE_LTX, var0.getDecreaseActualStorageLTXStatus());
        statusMap.put(UPDATE_ORDER_LTX, var0.getUpdateOrderLTXStatus());
        emptyOptMap.put(DECREASE_ACTUAL_STORAGE_LTX, var0.isDecreaseActualStorageLTXEmptyOpt());
        emptyOptMap.put(UPDATE_ORDER_LTX, var0.isUpdateOrderLTXEmptyOpt());
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(DecreaseActualStorageForConcludeEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(DECREASE_ACTUAL_STORAGE_LTX,e.getId());
            }
            else if(UpdateOrderForConcludeEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(UPDATE_ORDER_LTX,e.getId());
            }
        });

    }
}

