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
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
@Getter
public class DistributedTxRepresentation {
    protected Long id;
    protected DTXStatus status;
    protected String changeId;
    protected String orderId;
    protected long createdAt;
    private List<LtxRepresentation> ltx;

    public DistributedTxRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getDomainEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        ltx = var0.getLocalTxs().values().stream().map(ltx -> {
            Optional<StoredEvent> first = query.getData().stream().filter(e -> e.getName().equals(ltx.getName())).findFirst();
            return new LtxRepresentation(ltx.getName(), first.map(StoredEvent::getId).orElse(null), ltx.isEmptyOperation(), ltx.isSkipped(), ltx.getStatus());
        }).collect(Collectors.toList());
    }

    @Data
    private static class LtxRepresentation {
        private String name;
        private Long id;
        private boolean emptyOperation;
        private boolean skipped;
        private LTXStatus status;

        public LtxRepresentation(String name, Long id, boolean emptyOperation, boolean skipped, LTXStatus status) {
            this.name = name;
            this.id = id;
            this.status = status;
            this.emptyOperation = emptyOperation;
            this.skipped = skipped;
        }
    }
}
