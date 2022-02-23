package com.mt.saga.domain.model.reserve_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.appliction.reserve_order_dtx.command.DecreaseOrderStorageForReserveReplyEvent;
import com.mt.saga.appliction.reserve_order_dtx.command.UpdateOrderForReserveReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.order_state_machine.event.CreateReserveOrderDTXEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.reserve_order_dtx.event.DecreaseOrderStorageForReserveEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.ReserveOrderDTXFailedEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.ReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.UpdateOrderForReserveEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@NoArgsConstructor
public class ReserveOrderDTX extends Auditable implements Serializable {
    public static final String ENTITY_TX_NAME = "txName";
    public static final String ENTITY_TX_STATUS = "txStatus";
    public static final String ENTITY_REFERENCE_ID = "referenceId";
    @Id
    private Long id;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status;

    private String changeId;
    private String orderId;
    @Column(length = 25)
    @Convert(converter = BizOrderStatus.DBConverter.class)
    private BizOrderStatus orderStatus;
    private Integer orderVersion;
    @Lob
    private String orderCommandDetail;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus decreaseOrderStorageLTXStatus = LTXStatus.PENDING;
    private boolean decreaseOrderStorageLTXEmptyOpt=false;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus updateOrderLTXStatus = LTXStatus.PENDING;
    private boolean updateOrderLTXEmptyOpt=false;

    public ReserveOrderDTX(CreateReserveOrderDTXEvent event) {
        CommonOrderCommand command = event.getCommand();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.STARTED;
        this.changeId = command.getTxId();
        this.orderId = command.getOrderId();
        this.orderStatus=command.getOrderState();
        setOrderVersion(command.getVersion());
        orderCommandDetail = CommonDomainRegistry.getCustomObjectSerializer().serialize(command);
    }

    public void markAsStarted() {
        decreaseOrderStorageLTXStatus = LTXStatus.STARTED;
        updateOrderLTXStatus = LTXStatus.STARTED;
    }

    public void updateOrderLTXStatus(UpdateOrderForReserveReplyEvent next) {
        if(next.isEmptyOpt())
            this.updateOrderLTXEmptyOpt=true;
        this.updateOrderLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    public void updateSkuLTXStatus(DecreaseOrderStorageForReserveReplyEvent next) {
        if(next.isEmptyOpt())
            this.decreaseOrderStorageLTXEmptyOpt=true;
        this.decreaseOrderStorageLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    private void checkAll() {
            if (decreaseOrderStorageLTXStatus.equals(LTXStatus.SUCCESS) && updateOrderLTXStatus.equals(LTXStatus.SUCCESS)) {
                DomainRegistry.getIsolationService().removeActiveDtx(orderId);
                DomainEventPublisher.instance().publish(new ReserveOrderDTXSuccessEvent(this));
                this.status = DTXStatus.SUCCESS;
            }

    }

    public void retryStartedLtx() {
        if(getStatus().equals(DTXStatus.STARTED)){
            SumPagedRep<StoredEvent> relatedEvents = CommonDomainRegistry.getEventRepository().query(
                    new StoredEventQuery("domainId:" + getId(),
                            PageConfig.defaultConfig().getRawValue()
                            , QueryConfig.skipCount().value()));

            relatedEvents.getData().forEach(event->{
                if(getDecreaseOrderStorageLTXStatus().equals(LTXStatus.STARTED)){
                    if(DecreaseOrderStorageForReserveEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if(getUpdateOrderLTXStatus().equals(LTXStatus.STARTED)){
                    if(UpdateOrderForReserveEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
            });
        }
    }

    public void cancel() {
        if (getStatus().equals(DTXStatus.STARTED)) {
            DomainEventPublisher.instance().publish(new ReserveOrderDTXFailedEvent(this));
        } else {
            throw new IllegalStateException("can cancel started dtx only");
        }
    }
}
