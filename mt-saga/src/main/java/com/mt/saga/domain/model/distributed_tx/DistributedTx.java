package com.mt.saga.domain.model.distributed_tx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table
@Data
@NoArgsConstructor
public class DistributedTx extends Auditable implements AttributeConverter<Map<String, String>, String> {
    protected String name;
    @Id
    protected Long id;
    protected String changeId;
    protected String lockId;
    @Lob
    @Convert(converter = LocalTx.DBConverter.class)
    protected Map<String, LocalTx> localTxs;
    @Lob
    @Convert(converter = DistributedTx.class)
    protected Map<String, String> parameters = new HashMap<>();
    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    protected DTXStatus status = DTXStatus.PENDING;
    private Long forwardDtxId;
    private String resolveReason;
    private boolean isCancel;

    public DistributedTx(Set<LocalTx> localTxs, String name, String changeId, String lockId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.localTxs = localTxs.stream().collect(Collectors.toMap(LocalTx::getName, e -> e));
        this.name = name;
        this.changeId = changeId;
        this.lockId = lockId;
    }

    public static DistributedTx cancelOf(DistributedTx distributedTx, Set<LocalTx> localTxs) {
        DistributedTx distributedTx1 = new DistributedTx(localTxs, distributedTx.getName() + "_cancel", distributedTx.getChangeId() + "_cancel", distributedTx.getLockId());
        distributedTx1.replaceParams(distributedTx.getParameters());
        distributedTx1.forwardDtxId = distributedTx.getId();
        distributedTx1.isCancel = true;
        return distributedTx1;
    }

    public void updateParams(String key, String value) {
        parameters.put(key, value);
    }

    protected void replaceParams(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void handle(String name, ReplyEvent replyEvent) {
        LocalTx localTx = this.localTxs.get(name);
        localTx.handle(replyEvent);
        checkAll();
    }

    public void startLocalTx(String name) {
        LocalTx localTx = this.localTxs.get(name);
        localTx.start();
        startDtx();
    }

    public void startAllLocalTx() {
        this.localTxs.values().forEach(LocalTx::start);
        startDtx();
    }

    public boolean isLocalTxStarted(String name) {
        LocalTx localTx = this.localTxs.get(name);
        return localTx.getStatus().equals(LTXStatus.STARTED);
    }

    public LTXStatus getLocalTxStatusByName(String name) {
        LocalTx localTx = this.localTxs.get(name);
        return localTx.getStatus();
    }

    public boolean isLocalTxEmptyOptByName(String name) {
        LocalTx localTx = this.localTxs.get(name);
        return localTx.isEmptyOperation();
    }

    private void startDtx() {
        if (this.status.equals(DTXStatus.PENDING))
            this.status = DTXStatus.STARTED;
    }

    private void checkAll() {
        boolean b = this.localTxs.values().stream().allMatch(e -> e.getStatus().equals(LTXStatus.SUCCESS));
        if (b) {
            this.status = DTXStatus.SUCCESS;
            DomainRegistry.getIsolationService().removeActiveDtx(lockId);
            DomainEventPublisher.instance().publish(new DTXSuccessEvent(this));
        }
    }

    public void cancel(String topic) {
        if (isCancel) {
            throw new IllegalStateException("can not cancel cancel dtx");
        }
        if (status.equals(DTXStatus.STARTED)) {
            DomainEventPublisher.instance().publish(new DTXFailedEvent(this, topic));
        } else {
            throw new IllegalStateException("can cancel started dtx only");
        }
    }

    public void markAsResolved(ResolveReason resolveReason) {
        if (!isCancel) {
            throw new IllegalStateException("can not resolve forward dtx");
        }
        Validator.notBlank(resolveReason.getReason());
        if (status.equals(DTXStatus.STARTED)) {
            status = DTXStatus.RESOLVED;
            this.resolveReason = resolveReason.getReason();
        } else {
            throw new IllegalStateException("cannot mark dtx to resolve if not started");
        }
    }

    @Override
    public String convertToDatabaseColumn(Map<String, String> stringStringMap) {
        return CommonDomainRegistry.getCustomObjectSerializer().serialize(stringStringMap);
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String s) {
        return CommonDomainRegistry.getCustomObjectSerializer().deserializeToMap(s, String.class, String.class);
    }
}
