package com.mt.saga.domain.model.cancel_create_order_dtx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelClearCartReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelDecreaseOrderStorageForCreateReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelGeneratePaymentQRLinkReplyCommand;
import com.mt.saga.appliction.cancel_create_order_dtx.command.CancelSaveNewOrderReplyCommand;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelCreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.event.CreateOrderDTXFailedEvent;
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
public class CancelCreateOrderDTX extends Auditable implements Serializable {
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
    private Long forwardDtxId;

    @Lob
    private String orderCommandDetail;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelSaveNewOrderLTXStatus = LTXStatus.PENDING;
    private boolean cancelSaveNewOrderLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelDecreaseOrderStorageLTXStatus = LTXStatus.PENDING;
    private boolean cancelDecreaseOrderStorageLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelGeneratePaymentLinkLTXStatus = LTXStatus.PENDING;
    private boolean cancelGeneratePaymentLinkLTXEmptyOpt = false;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelRemoveItemsFromCartLTXStatus = LTXStatus.PENDING;
    private boolean cancelRemoveItemsFromCartLTXEmptyOpt = false;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private String resolveReason;

    public CancelCreateOrderDTX(CreateOrderDTXFailedEvent event) {
        CreateOrderDTX dtx = event.getCreateOrderDTX();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.PENDING;
        this.changeId = dtx.getChangeId() + "_cancel";
        this.orderId = dtx.getOrderId();
        this.forwardDtxId = dtx.getId();
        orderCommandDetail = dtx.getOrderCommandDetail();
    }


    public void checkAll() {
        if (cancelSaveNewOrderLTXStatus.equals(LTXStatus.SUCCESS)
                && cancelDecreaseOrderStorageLTXStatus.equals(LTXStatus.SUCCESS)
                && cancelRemoveItemsFromCartLTXStatus.equals(LTXStatus.SUCCESS)
                && cancelGeneratePaymentLinkLTXStatus.equals(LTXStatus.SUCCESS)) {
            DomainEventPublisher.instance().publish(new CancelCreateOrderDTXSuccessEvent(this));
            setStatus(DTXStatus.SUCCESS);
        }
    }

    public void markAsStarted() {
        this.status = DTXStatus.STARTED;
        this.cancelDecreaseOrderStorageLTXStatus = LTXStatus.STARTED;
        this.cancelRemoveItemsFromCartLTXStatus = LTXStatus.STARTED;
        this.cancelGeneratePaymentLinkLTXStatus = LTXStatus.STARTED;
        this.cancelSaveNewOrderLTXStatus = LTXStatus.STARTED;
    }

    public void handle(CancelGeneratePaymentQRLinkReplyCommand command) {
        if(command.isEmptyOpt())
            setCancelGeneratePaymentLinkLTXEmptyOpt(true);
        setCancelGeneratePaymentLinkLTXStatus( LTXStatus.SUCCESS);
        checkAll();
    }

    public void handle(CancelDecreaseOrderStorageForCreateReplyCommand deserialize) {
        if(deserialize.isEmptyOpt())
            setCancelDecreaseOrderStorageLTXEmptyOpt(true);
        setCancelDecreaseOrderStorageLTXStatus( LTXStatus.SUCCESS);
        checkAll();
    }

    public void handle(CancelClearCartReplyCommand deserialize) {
        if(deserialize.isEmptyOpt())
            setCancelRemoveItemsFromCartLTXEmptyOpt(true);
        setCancelRemoveItemsFromCartLTXStatus( LTXStatus.SUCCESS);
        checkAll();
    }

    public void handle(CancelSaveNewOrderReplyCommand deserialize) {
        if(deserialize.isEmptyOpt())
            setCancelSaveNewOrderLTXEmptyOpt(true);
        setCancelSaveNewOrderLTXStatus( LTXStatus.SUCCESS);
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
