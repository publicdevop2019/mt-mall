package com.mt.saga.domain.model.distributed_tx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.saga.appliction.distributed_tx.command.ReplyEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class LocalTx {
    private String name;
    private String eventName;
    private boolean emptyOperation = false;
    private boolean skipped = false;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus status = LTXStatus.PENDING;

    public LocalTx(String name) {
        this.name = name;
        this.eventName = name;
    }

    public void handle(ReplyEvent replyEvent) {
        if (!status.equals(LTXStatus.STARTED)) {
            throw new IllegalArgumentException("can not mark non start tx as success");
        }
        status = LTXStatus.SUCCESS;
        emptyOperation = replyEvent.isEmptyOpt();
    }

    protected void start() {
        status = LTXStatus.STARTED;
    }

    public void skip() {
        status = LTXStatus.SUCCESS;
        emptyOperation = true;
        skipped = true;
    }


    public static class DBConverter implements AttributeConverter<Map<String, LocalTx>, String> {
        @Override
        public String convertToDatabaseColumn(Map<String, LocalTx> localTxes) {
            return CommonDomainRegistry.getCustomObjectSerializer().serializeCollection(localTxes.values());
        }

        @Override
        public Map<String, LocalTx> convertToEntityAttribute(String s) {
            Collection<LocalTx> localTxes = CommonDomainRegistry.getCustomObjectSerializer().deserializeCollection(s, LocalTx.class);
            return localTxes.stream().collect(Collectors.toMap(LocalTx::getName, e -> e));
        }
    }
}
