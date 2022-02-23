package com.mt.saga.appliction.cancel_conclude_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelDecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelUpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelConcludeOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_UPDATE_ORDER_LTX = "CANCEL_UPDATE_ORDER_LTX";
    private static final String CANCEL_DECREASE_ACTUAL_STORAGE = "CANCEL_DECREASE_ACTUAL_STORAGE_LTX";

    public CancelConcludeOrderDTXRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_UPDATE_ORDER_LTX, var0.getLocalTxStatusByName(CancelUpdateOrderForConcludeEvent.name));
        statusMap.put(CANCEL_DECREASE_ACTUAL_STORAGE, var0.getLocalTxStatusByName(CancelDecreaseActualStorageForConcludeEvent.name));
        emptyOptMap.put(CANCEL_UPDATE_ORDER_LTX, var0.isLocalTxEmptyOptByName(CancelUpdateOrderForConcludeEvent.name));
        emptyOptMap.put(CANCEL_DECREASE_ACTUAL_STORAGE, var0.isLocalTxEmptyOptByName(CancelDecreaseActualStorageForConcludeEvent.name));
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(CancelUpdateOrderForConcludeEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_UPDATE_ORDER_LTX,e.getId());
            }
            else if(CancelDecreaseActualStorageForConcludeEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_DECREASE_ACTUAL_STORAGE,e.getId());
            }
        });
    }
}
