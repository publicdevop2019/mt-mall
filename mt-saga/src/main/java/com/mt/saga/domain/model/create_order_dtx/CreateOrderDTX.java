package com.mt.saga.domain.model.create_order_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.create_order_dtx.command.ClearCartReplyCommand;
import com.mt.saga.appliction.create_order_dtx.command.DecreaseOrderStorageForCreateReplyCommand;
import com.mt.saga.appliction.create_order_dtx.command.GeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.create_order_dtx.command.SaveNewOrderReplyCommand;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXFailedEvent;
import com.mt.saga.domain.model.create_order_dtx.event.*;
import com.mt.saga.domain.model.order_state_machine.event.CreateCreateOrderDTXEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
@Data
@NoArgsConstructor
public class CreateOrderDTX extends Auditable implements Serializable {
    @Id
    private Long id;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status;

    private String changeId;
    private String orderId;

    @Lob
    private String orderCommandDetail;
    private LTXStatus saveNewOrderLTXStatus = LTXStatus.PENDING;
    private boolean saveNewOrderLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus decreaseOrderStorageLTXStatus = LTXStatus.PENDING;
    private boolean decreaseOrderStorageLTXEmptyOpt = false;
    @Embedded
    private GeneratePaymentLinkLTX generatePaymentLinkLTX;
    private boolean generatePaymentLinkLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus clearCartLtx = LTXStatus.PENDING;
    private boolean clearCartEmptyOpt = false;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public CreateOrderDTX(CreateCreateOrderDTXEvent event) {
        CommonOrderCommand command = event.getCommand();
        String serialize = CommonDomainRegistry.getCustomObjectSerializer().serialize(command);
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.PENDING;
        this.changeId = command.getTxId();
        this.orderId = command.getOrderId();
        orderCommandDetail = serialize;
        generatePaymentLinkLTX = new GeneratePaymentLinkLTX();
    }


    public void checkAll() {
        if (saveNewOrderLTXStatus.equals(LTXStatus.SUCCESS)
                && decreaseOrderStorageLTXStatus.equals(LTXStatus.SUCCESS)
                && clearCartLtx.equals(LTXStatus.SUCCESS)
                && generatePaymentLinkLTX.getStatus().equals(LTXStatus.SUCCESS)) {
            DomainRegistry.getIsolationService().removeActiveDtx(orderId);
            DomainEventPublisher.instance().publish(new CreateOrderDTXSuccessEvent(this));
            setStatus(DTXStatus.SUCCESS);
        }
    }

    public void markAsStarted() {
        this.status = DTXStatus.STARTED;
        this.decreaseOrderStorageLTXStatus = LTXStatus.STARTED;
        this.clearCartLtx = LTXStatus.STARTED;
        this.generatePaymentLinkLTX.setStatus(LTXStatus.STARTED);
    }

    public void handle(ClearCartReplyCommand command) {
        if (command.isEmptyOpt())
            this.clearCartEmptyOpt = true;
        setClearCartLtx(LTXStatus.SUCCESS);
        checkAll();
    }

    public void handle(DecreaseOrderStorageForCreateReplyCommand command) {
        if (command.isEmptyOpt())
            this.decreaseOrderStorageLTXEmptyOpt = true;
        setDecreaseOrderStorageLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }

    public void handle(GeneratePaymentQRLinkReplyCommand command) {
        if (command.isEmptyOpt())
            this.generatePaymentLinkLTXEmptyOpt = true;
        getGeneratePaymentLinkLTX().setResults(command.getPaymentLink());
        getGeneratePaymentLinkLTX().setStatus(LTXStatus.SUCCESS);
        SaveNewOrderEvent event = new SaveNewOrderEvent(command,this);
        setSaveNewOrderLTXStatus(LTXStatus.STARTED);
        DomainEventPublisher.instance().publish(event);
        checkAll();
    }

    public void handle(SaveNewOrderReplyCommand command) {
        if (command.isEmptyOpt())
            this.saveNewOrderLTXEmptyOpt = true;
        setSaveNewOrderLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }

    public void retryStartedLtx() {
        if(getStatus().equals(DTXStatus.STARTED)){
            SumPagedRep<StoredEvent> relatedEvents = CommonDomainRegistry.getEventRepository().query(
                    new StoredEventQuery("domainId:" + getId(),
                            PageConfig.defaultConfig().getRawValue()
                            , QueryConfig.skipCount().value()));

            relatedEvents.getData().forEach(event->{
                if(getDecreaseOrderStorageLTXStatus().equals(LTXStatus.STARTED)){
                    if(DecreaseOrderStorageForCreateEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if(getGeneratePaymentLinkLTX().getStatus().equals(LTXStatus.STARTED)){
                    if(GeneratePaymentQRLinkEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if(getSaveNewOrderLTXStatus().equals(LTXStatus.STARTED)){
                    if(SaveNewOrderEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if(getClearCartLtx().equals(LTXStatus.STARTED)){
                    if(ClearCartEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
            });
        }
    }

    public void cancel() {
        if (getStatus().equals(DTXStatus.STARTED)) {
        DomainEventPublisher.instance().publish(new CreateOrderDTXFailedEvent(this));
        } else {
            throw new IllegalStateException("can cancel started dtx only");
        }
    }
}
