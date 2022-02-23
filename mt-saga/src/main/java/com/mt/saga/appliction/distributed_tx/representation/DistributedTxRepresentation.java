package com.mt.saga.appliction.distributed_tx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.domain.model.distributed_tx.DTXStatus;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.LTXStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Setter
@Getter
public class DistributedTxRepresentation {
    protected Long id;
    protected DTXStatus status;
    protected String changeId;
    protected String orderId;
    protected long createdAt;
    protected Map<String, LTXStatus> statusMap = new HashMap<>();
    protected Map<String, Boolean> emptyOptMap = new HashMap<>();
    protected Map<String, Long> idMap = new HashMap<>();

    public DistributedTxRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        var0.getLocalTxs().values().forEach(ltx -> {
            statusMap.put(ltx.getName(), ltx.getStatus());
            emptyOptMap.put(ltx.getName(), ltx.isEmptyOperation());
            Optional<StoredEvent> first = query.getData().stream().filter(e -> e.getName().equals(ltx.getName())).findFirst();
            first.ifPresent(e -> {
                idMap.put(ltx.getName(), e.getId());
            });
        });
    }
}
