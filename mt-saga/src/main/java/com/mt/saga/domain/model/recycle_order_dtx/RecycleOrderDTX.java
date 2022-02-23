package com.mt.saga.domain.model.recycle_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.recycle_order_dtx.command.IncreaseOrderStorageForRecycleReplyEvent;
import com.mt.saga.appliction.recycle_order_dtx.command.UpdateOrderForRecycleReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.order_state_machine.event.CreateRecycleOrderDTXEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.IncreaseOrderStorageForRecycleEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXFailedEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.recycle_order_dtx.event.UpdateOrderForRecycleEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@NoArgsConstructor
public class RecycleOrderDTX extends Auditable implements Serializable {
    public static final String ENTITY_TX_NAME = "txName";
    public static final String ENTITY_TX_STATUS = "txStatus";
    public static final String ENTITY_REFERENCE_ID = "referenceId";
    @Id
    private Long id;


    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status;

    private String changeId;

    @Lob
    private String createBizStateMachineCommand;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus increaseOrderStorageLTXStatus = LTXStatus.PENDING;
    private boolean increaseOrderStorageLTXEmptyOpt=false;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus updateOrderLTXStatus = LTXStatus.PENDING;
    private boolean updateOrderLTXEmptyOpt=false;

    private String orderId;
    private Integer orderVersion;

    public RecycleOrderDTX(CreateRecycleOrderDTXEvent deserialize) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.STARTED;
        this.changeId = deserialize.getCommand().getTxId();
        this.orderId = deserialize.getCommand().getOrderId();
        this.orderVersion = deserialize.getCommand().getVersion();
        createBizStateMachineCommand = CommonDomainRegistry.getCustomObjectSerializer().serialize(deserialize.getCommand());
    }

    public void markAsStarted() {
        this.increaseOrderStorageLTXStatus = LTXStatus.STARTED;
        this.updateOrderLTXStatus = LTXStatus.STARTED;
    }

    public void handle(UpdateOrderForRecycleReplyEvent event) {
        if(event.isEmptyOpt())
            this.updateOrderLTXEmptyOpt=true;
        this.updateOrderLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    public void handle(IncreaseOrderStorageForRecycleReplyEvent event) {
        if(event.isEmptyOpt())
            this.increaseOrderStorageLTXEmptyOpt=true;
        this.increaseOrderStorageLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    private void checkAll() {
        if (increaseOrderStorageLTXStatus.equals(LTXStatus.SUCCESS) && updateOrderLTXStatus.equals(LTXStatus.SUCCESS)) {
            DomainRegistry.getIsolationService().removeActiveDtx(orderId);
            DomainEventPublisher.instance().publish(new RecycleOrderDTXSuccessEvent(this));
            this.setStatus(DTXStatus.SUCCESS);
        }
    }

    public void retryStartedLtx() {
        if(getStatus().equals(DTXStatus.STARTED)){
            SumPagedRep<StoredEvent> relatedEvents = CommonDomainRegistry.getEventRepository().query(
                    new StoredEventQuery("domainId:" + getId(),
                            PageConfig.defaultConfig().getRawValue()
                            , QueryConfig.skipCount().value()));

            relatedEvents.getData().forEach(event->{
                if(getIncreaseOrderStorageLTXStatus().equals(LTXStatus.STARTED)){
                    if(IncreaseOrderStorageForRecycleEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if(getUpdateOrderLTXStatus().equals(LTXStatus.STARTED)){
                    if(UpdateOrderForRecycleEvent.name.equals(event.getName())){
                           CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
            });
        }
    }

    public void cancel() {
        if (getStatus().equals(DTXStatus.STARTED)) {
        DomainEventPublisher.instance().publish(new RecycleOrderDTXFailedEvent(this));
        } else {
            throw new IllegalStateException("can cancel started dtx only");
        }
    }
}
