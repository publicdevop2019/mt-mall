package com.mt.saga.domain.model.conclude_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.conclude_order_dtx.command.DecreaseActualStorageForConcludeReplyEvent;
import com.mt.saga.appliction.conclude_order_dtx.command.UpdateOrderForConcludeReplyEvent;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXFailedEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.DecreaseActualStorageForConcludeEvent;
import com.mt.saga.domain.model.conclude_order_dtx.event.UpdateOrderForConcludeEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateConcludeOrderDTXEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@Slf4j
@NoArgsConstructor
public class ConcludeOrderDTX extends Auditable implements Serializable {
    public static final String ENTITY_TX_STATUS = "txStatus";
    public static final String ENTITY_REFERENCE_ID = "referenceId";
    @Id
    private Long id;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status = DTXStatus.PENDING;

    private String changeId;
    private String orderId;
    private Integer orderVersion;

    @Lob
    private String createBizStateMachineCommand;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus decreaseActualStorageLTXStatus = LTXStatus.PENDING;
    private boolean decreaseActualStorageLTXEmptyOpt = false;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus updateOrderLTXStatus = LTXStatus.PENDING;
    private boolean updateOrderLTXEmptyOpt = false;

    public ConcludeOrderDTX(CreateConcludeOrderDTXEvent deserialize) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.changeId = deserialize.getCommand().getTxId();
        this.orderId = deserialize.getCommand().getOrderId();
        this.orderVersion = deserialize.getCommand().getVersion();
        CommonOrderCommand command = deserialize.getCommand();
        createBizStateMachineCommand = CommonDomainRegistry.getCustomObjectSerializer().serialize(command);
    }

    public void handle(UpdateOrderForConcludeReplyEvent event) {
        if (event.isEmptyOpt())
            this.updateOrderLTXEmptyOpt = true;
        this.updateOrderLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    public void handle(DecreaseActualStorageForConcludeReplyEvent event) {
        if (event.isEmptyOpt())
            this.decreaseActualStorageLTXEmptyOpt = true;
        this.decreaseActualStorageLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    private void checkAll() {
        if (decreaseActualStorageLTXStatus.equals(LTXStatus.SUCCESS) && updateOrderLTXStatus.equals(LTXStatus.SUCCESS)) {
            this.setStatus(DTXStatus.SUCCESS);
            DomainRegistry.getIsolationService().removeActiveDtx(orderId);

            DomainEventPublisher.instance().publish(new ConcludeOrderDTXSuccessEvent(this));
        }
    }

    public void markAsStarted() {
        this.decreaseActualStorageLTXStatus = LTXStatus.STARTED;
        this.updateOrderLTXStatus = LTXStatus.STARTED;
        this.status = DTXStatus.STARTED;
    }

    public void retryStartedLtx() {
        if (getStatus().equals(DTXStatus.STARTED)) {
            SumPagedRep<StoredEvent> relatedEvents = CommonDomainRegistry.getEventRepository().query(
                    new StoredEventQuery("domainId:" + getId(),
                            PageConfig.defaultConfig().getRawValue()
                            , QueryConfig.skipCount().value()));

            relatedEvents.getData().forEach(event -> {
                if (getDecreaseActualStorageLTXStatus().equals(LTXStatus.STARTED)) {
                    if (DecreaseActualStorageForConcludeEvent.name.equals(event.getName())) {
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if (getUpdateOrderLTXStatus().equals(LTXStatus.STARTED)) {
                    if (UpdateOrderForConcludeEvent.name.equals(event.getName())) {
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
            });
        }
    }

    public void cancel() {
        if (getStatus().equals(DTXStatus.STARTED)) {
            DomainEventPublisher.instance().publish(new ConcludeOrderDTXFailedEvent(this));
        } else {
            throw new IllegalStateException("can cancel started dtx only");
        }
    }
}
