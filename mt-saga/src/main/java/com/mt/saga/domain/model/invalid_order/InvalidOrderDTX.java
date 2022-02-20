package com.mt.saga.domain.model.invalid_order;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.invalid_order_dtx.command.*;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.invalid_order.event.*;
import com.mt.saga.domain.model.order_state_machine.event.CreateInvalidOrderDTXEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
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
public class InvalidOrderDTX extends Auditable implements Serializable {
    @Id
    private Long id;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status;

    private String changeId;
    private String orderId;

    @Lob
    private String orderCommandDetail;
    private LTXStatus removeOrderLTXStatus = LTXStatus.PENDING;
    private boolean removeOrderLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus restoreCartLTXStatus = LTXStatus.PENDING;
    private boolean restoreCartLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus increaseStorageLTXStatus = LTXStatus.PENDING;
    private boolean increaseStorageLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus removePaymentLinkLTXStatus = LTXStatus.PENDING;
    private boolean removePaymentLinkLTXEmptyOpt = false;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private Integer orderVersion;
    public InvalidOrderDTX(CreateInvalidOrderDTXEvent event) {
        CommonOrderCommand command = event.getCommand();
        String serialize = CommonDomainRegistry.getCustomObjectSerializer().serialize(command);
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.PENDING;
        this.changeId = command.getTxId();
        this.orderId = command.getOrderId();
        setOrderVersion(command.getVersion());
        orderCommandDetail = serialize;
        this.status = DTXStatus.STARTED;
        if (command.getOrderState().equals(BizOrderStatus.PAID_RECYCLED)) {
            DomainEventPublisher.instance().publish(new RemovePaymentQRLinkForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(this));
            this.increaseStorageLTXStatus = LTXStatus.SUCCESS;
            this.increaseStorageLTXEmptyOpt =true;
            this.restoreCartLTXStatus = LTXStatus.STARTED;
            this.removePaymentLinkLTXStatus = LTXStatus.STARTED;
            this.removeOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.PAID_RESERVED)) {
            DomainEventPublisher.instance().publish(new RemovePaymentQRLinkForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new IncreaseStorageForInvalidEvent(this));
            this.increaseStorageLTXStatus = LTXStatus.STARTED;
            this.restoreCartLTXStatus = LTXStatus.STARTED;
            this.removePaymentLinkLTXStatus = LTXStatus.STARTED;
            this.removeOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RESERVED)) {
            DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new IncreaseStorageForInvalidEvent(this));
            this.removePaymentLinkLTXStatus = LTXStatus.SUCCESS;
            this.removePaymentLinkLTXEmptyOpt = true;
            this.increaseStorageLTXStatus = LTXStatus.STARTED;
            this.restoreCartLTXStatus = LTXStatus.STARTED;
            this.removeOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RECYCLED)) {
            DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(this));
            this.removePaymentLinkLTXStatus = LTXStatus.SUCCESS;
            this.removePaymentLinkLTXEmptyOpt = true;
            this.increaseStorageLTXStatus = LTXStatus.SUCCESS;
            this.increaseStorageLTXEmptyOpt = true;
            this.restoreCartLTXStatus = LTXStatus.STARTED;
            this.removeOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.CONFIRMED)) {
            DomainEventPublisher.instance().publish(new RemovePaymentQRLinkForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new RemoveOrderForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new IncreaseStorageForInvalidEvent(this));
            this.increaseStorageLTXStatus = LTXStatus.STARTED;
            this.restoreCartLTXStatus = LTXStatus.STARTED;
            this.removePaymentLinkLTXStatus = LTXStatus.STARTED;
            this.removeOrderLTXStatus = LTXStatus.STARTED;
        }
    }


    public void checkAll() {
        if (removeOrderLTXStatus.equals(LTXStatus.SUCCESS)
                && increaseStorageLTXStatus.equals(LTXStatus.SUCCESS)
                && restoreCartLTXStatus.equals(LTXStatus.SUCCESS)
                && removePaymentLinkLTXStatus.equals(LTXStatus.SUCCESS)) {
            DomainRegistry.getIsolationService().removeActiveDtx(orderId);
            DomainEventPublisher.instance().publish(new InvalidOrderDTXSuccessEvent(this));
            setStatus(DTXStatus.SUCCESS);
        }
    }

    public void handle(IncreaseStorageForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setIncreaseStorageLTXEmptyOpt(true);
        setIncreaseStorageLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }

    public void handle(RemoveOrderForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setRemoveOrderLTXEmptyOpt(true);
        setRemoveOrderLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }
    public void handle(RemovePaymentQRLinkForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setRemovePaymentLinkLTXEmptyOpt(true);
        setRemovePaymentLinkLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }
    public void handle(RestoreCartForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setRestoreCartLTXEmptyOpt(true);
        setRestoreCartLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }




    public void retryStartedLtx() {
        if (getStatus().equals(DTXStatus.STARTED)) {
            SumPagedRep<StoredEvent> relatedEvents = CommonDomainRegistry.getEventRepository().query(
                    new StoredEventQuery("domainId:" + getId(),
                            PageConfig.defaultConfig().getRawValue()
                            , QueryConfig.skipCount().value()));

            relatedEvents.getData().forEach(event -> {
                if (getIncreaseStorageLTXStatus().equals(LTXStatus.STARTED)) {
                    if (IncreaseStorageForInvalidEvent.name.equals(event.getName())) {
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if (getRemovePaymentLinkLTXStatus().equals(LTXStatus.STARTED)) {
                    if (RemovePaymentQRLinkForInvalidEvent.name.equals(event.getName())) {
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if (getRemoveOrderLTXStatus().equals(LTXStatus.STARTED)) {
                    if (RemoveOrderForInvalidEvent.name.equals(event.getName())) {
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
                if (getRestoreCartLTXStatus().equals(LTXStatus.STARTED)) {
                    if (RestoreCartForInvalidEvent.name.equals(event.getName())) {
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
            });
        }
    }

    public void cancel() {
        if (getStatus().equals(DTXStatus.STARTED)) {
            DomainEventPublisher.instance().publish(new InvalidOrderDTXFailedEvent(this));
        } else {
            throw new IllegalStateException("can cancel started dtx only");
        }
    }
}