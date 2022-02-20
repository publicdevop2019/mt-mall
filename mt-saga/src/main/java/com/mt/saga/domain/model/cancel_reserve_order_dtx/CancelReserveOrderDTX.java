package com.mt.saga.domain.model.cancel_reserve_order_dtx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.cancel_reserve_order_dtx.command.CancelDecreaseOrderStorageForReserveReplyEvent;
import com.mt.saga.appliction.cancel_reserve_order_dtx.command.CancelUpdateOrderForReserveReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.reserve_order_dtx.event.ReserveOrderDTXFailedEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@NoArgsConstructor
public class CancelReserveOrderDTX extends Auditable implements Serializable {
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
    private long forwardDtxId;
    private Integer orderVersion;
    @Lob
    private String orderCommandDetail;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelDecreaseOrderStorageLTXStatus = LTXStatus.PENDING;
    private boolean cancelDecreaseOrderStorageLTXEmptyOpt = false;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelUpdateOrderLTXStatus = LTXStatus.PENDING;
    private boolean cancelUpdateOrderLTXEmptyOpt = false;
    private String resolveReason;

    public CancelReserveOrderDTX(ReserveOrderDTXFailedEvent deserialize) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.forwardDtxId = deserialize.getReserveOrderDTX().getId();
        this.status = DTXStatus.STARTED;
        this.changeId = deserialize.getReserveOrderDTX().getChangeId() + AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        this.orderId = deserialize.getReserveOrderDTX().getOrderId();
        setOrderVersion(deserialize.getReserveOrderDTX().getOrderVersion());
        orderCommandDetail = deserialize.getReserveOrderDTX().getOrderCommandDetail();

    }

    public void markAsStarted() {
        cancelDecreaseOrderStorageLTXStatus = LTXStatus.STARTED;
        cancelUpdateOrderLTXStatus = LTXStatus.STARTED;
    }

    public void handle(CancelUpdateOrderForReserveReplyEvent next) {
        if (next.isEmptyOpt()){
            setCancelUpdateOrderLTXEmptyOpt(true);
        }
        this.cancelUpdateOrderLTXStatus = LTXStatus.SUCCESS;

        checkAll();
    }

    public void handle(CancelDecreaseOrderStorageForReserveReplyEvent next) {
        if (next.isEmptyOpt()){
            setCancelDecreaseOrderStorageLTXEmptyOpt(true);
        }
        this.cancelDecreaseOrderStorageLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    private void checkAll() {
        if (cancelDecreaseOrderStorageLTXStatus.equals(LTXStatus.SUCCESS) && cancelUpdateOrderLTXStatus.equals(LTXStatus.SUCCESS)) {
            DomainEventPublisher.instance().publish(new CancelReserveOrderDTXSuccessEvent(this));
            this.status = DTXStatus.SUCCESS;
        }

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
