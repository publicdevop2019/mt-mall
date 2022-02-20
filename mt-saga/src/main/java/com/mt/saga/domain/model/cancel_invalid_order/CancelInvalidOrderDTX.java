package com.mt.saga.domain.model.cancel_invalid_order;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.cancel_invalid_order_dtx.command.*;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_invalid_order.event.*;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.invalid_order.event.*;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.infrastructure.AppConstant;
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
public class CancelInvalidOrderDTX  extends Auditable implements Serializable {
    @Id
    private Long id;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status;

    private String changeId;
    private String orderId;
    private Long forwardDtxId;
    @Lob
    private String orderCommandDetail;
    private LTXStatus cancelRemoveOrderLTXStatus = LTXStatus.PENDING;
    private boolean cancelRemoveOrderLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelRestoreCartLTXStatus = LTXStatus.PENDING;
    private boolean cancelRestoreCartLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelIncreaseStorageLTXStatus = LTXStatus.PENDING;
    private boolean cancelIncreaseStorageLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelRemovePaymentLinkLTXStatus = LTXStatus.PENDING;
    private boolean cancelRemovePaymentLinkLTXEmptyOpt = false;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private Integer orderVersion;
    private String resolveReason;
    public CancelInvalidOrderDTX(InvalidOrderDTXFailedEvent event) {
        InvalidOrderDTX dtx = event.getDtx();
        CommonOrderCommand command = CommonDomainRegistry.getCustomObjectSerializer().deserialize(dtx.getOrderCommandDetail(), CommonOrderCommand.class);
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.PENDING;
        this.changeId = command.getTxId()+ AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        this.orderId = command.getOrderId();
        setOrderVersion(command.getVersion());
        setForwardDtxId(dtx.getId());
        orderCommandDetail = dtx.getOrderCommandDetail();
        this.status = DTXStatus.STARTED;
        if (command.getOrderState().equals(BizOrderStatus.PAID_RECYCLED)) {
            DomainEventPublisher.instance().publish(new CancelRemovePaymentQRLinkForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(this));
            this.cancelIncreaseStorageLTXStatus = LTXStatus.SUCCESS;
            this.cancelIncreaseStorageLTXEmptyOpt = true;
            this.cancelRestoreCartLTXStatus = LTXStatus.STARTED;
            this.cancelRemovePaymentLinkLTXStatus = LTXStatus.STARTED;
            this.cancelRemoveOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.PAID_RESERVED)) {
            DomainEventPublisher.instance().publish(new CancelRemovePaymentQRLinkForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelIncreaseStorageForInvalidEvent(this));
            this.cancelIncreaseStorageLTXStatus = LTXStatus.STARTED;
            this.cancelRestoreCartLTXStatus = LTXStatus.STARTED;
            this.cancelRemovePaymentLinkLTXStatus = LTXStatus.STARTED;
            this.cancelRemoveOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RESERVED)) {
            DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelIncreaseStorageForInvalidEvent(this));
            this.cancelRemovePaymentLinkLTXStatus = LTXStatus.SUCCESS;
            this.cancelRemovePaymentLinkLTXEmptyOpt = true;
            this.cancelIncreaseStorageLTXStatus = LTXStatus.STARTED;
            this.cancelRestoreCartLTXStatus = LTXStatus.STARTED;
            this.cancelRemoveOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.NOT_PAID_RECYCLED)) {
            DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(this));
            this.cancelRemovePaymentLinkLTXStatus = LTXStatus.SUCCESS;
            this.cancelRemovePaymentLinkLTXEmptyOpt = true;
            this.cancelIncreaseStorageLTXStatus = LTXStatus.SUCCESS;
            this.cancelIncreaseStorageLTXEmptyOpt = true;
            this.cancelRestoreCartLTXStatus = LTXStatus.STARTED;
            this.cancelRemoveOrderLTXStatus = LTXStatus.STARTED;
        } else if (command.getOrderState().equals(BizOrderStatus.CONFIRMED)) {
            DomainEventPublisher.instance().publish(new CancelRemovePaymentQRLinkForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRestoreCartForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelRemoveOrderForInvalidEvent(this));
            DomainEventPublisher.instance().publish(new CancelIncreaseStorageForInvalidEvent(this));
            this.cancelIncreaseStorageLTXStatus = LTXStatus.STARTED;
            this.cancelRestoreCartLTXStatus = LTXStatus.STARTED;
            this.cancelRemovePaymentLinkLTXStatus = LTXStatus.STARTED;
            this.cancelRemoveOrderLTXStatus = LTXStatus.STARTED;
        }
    }


    public void checkAll() {
        if (cancelRemoveOrderLTXStatus.equals(LTXStatus.SUCCESS)
                && cancelIncreaseStorageLTXStatus.equals(LTXStatus.SUCCESS)
                && cancelRestoreCartLTXStatus.equals(LTXStatus.SUCCESS)
                && cancelRemovePaymentLinkLTXStatus.equals(LTXStatus.SUCCESS)) {
            DomainEventPublisher.instance().publish(new CancelInvalidOrderDTXSuccessEvent(this));
            setStatus(DTXStatus.SUCCESS);
        }
    }

    public void handle(CancelIncreaseStorageForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setCancelIncreaseStorageLTXEmptyOpt(true);
        setCancelIncreaseStorageLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }

    public void handle(CancelRemoveOrderForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setCancelRemoveOrderLTXEmptyOpt(true);
        setCancelRemoveOrderLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }
    public void handle(CancelRemovePaymentQRLinkForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setCancelRemovePaymentLinkLTXEmptyOpt(true);
        setCancelRemovePaymentLinkLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }
    public void handle(CancelRestoreCartForInvalidReplyEvent command) {
        if (command.isEmptyOpt())
            setCancelRestoreCartLTXEmptyOpt(true);
        setCancelRestoreCartLTXStatus(LTXStatus.SUCCESS);
        checkAll();
    }
    public void markAsResolved(ResolveReason resolveReason) {
        Validator.notBlank(resolveReason.getReason());
        if(status.equals(DTXStatus.STARTED)){
            setStatus(DTXStatus.RESOLVED);
            setResolveReason(resolveReason.getReason());
        }else{
            throw new IllegalStateException("cannot mark dtx to resolve if not started");
        }
    }




}