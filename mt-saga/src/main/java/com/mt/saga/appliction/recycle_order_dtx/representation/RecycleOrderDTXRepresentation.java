package com.mt.saga.appliction.recycle_order_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.recycle_order_dtx.event.IncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.UpdateOrderForRecycleEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecycleOrderDTXRepresentation extends CommonDTXRepresentation {

    private static final String UPDATE_ORDER_LTX = "UPDATE_ORDER_LTX";
    private static final String INCREASE_ORDER_STORAGE_LTX = "INCREASE_ORDER_STORAGE_LTX";

    public RecycleOrderDTXRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(UPDATE_ORDER_LTX, var0.getLocalTxStatusByName(UpdateOrderForRecycleEvent.name));
        statusMap.put(INCREASE_ORDER_STORAGE_LTX, var0.getLocalTxStatusByName(IncreaseOrderStorageForRecycleEvent.name));
        emptyOptMap.put(UPDATE_ORDER_LTX, var0.isLocalTxEmptyOptByName(UpdateOrderForRecycleEvent.name));
        emptyOptMap.put(INCREASE_ORDER_STORAGE_LTX, var0.isLocalTxEmptyOptByName(IncreaseOrderStorageForRecycleEvent.name));
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e -> {
            if (IncreaseOrderStorageForRecycleEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(INCREASE_ORDER_STORAGE_LTX, e.getId());
            } else if (UpdateOrderForRecycleEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(UPDATE_ORDER_LTX, e.getId());
            }
        });

    }
}

