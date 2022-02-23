package com.mt.saga.domain.model.cancel_recycle_dtx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelIncreaseOrderStorageForRecycleReplyEvent;
import com.mt.saga.appliction.cancel_recycle_order_dtx.command.CancelUpdateOrderForRecycleReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelRecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@NoArgsConstructor
public class CancelRecycleOrderDTX extends Auditable implements Serializable {
    public static final String ENTITY_TX_STATUS = "txStatus";
    public static final String ENTITY_REFERENCE_ID = "referenceId";
    @Id
    private Long id;
    private Long forwardDtxId;


    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status;

    private String changeId;

    @Lob
    private String createBizStateMachineCommand;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelIncreaseOrderStorageLTXStatus = LTXStatus.PENDING;
    private boolean cancelIncreaseOrderStorageLTXEmptyOpt = false;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelUpdateOrderLTXStatus = LTXStatus.PENDING;
    private boolean cancelUpdateOrderLTXEmptyOpt = false;

    private String orderId;
    private int orderVersion;
    private String resolveReason;
    public CancelRecycleOrderDTX(RecycleOrderDTXFailedEvent deserialize) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.STARTED;
        this.forwardDtxId = deserialize.getRecycleOrderDTX().getId();
        this.changeId = deserialize.getRecycleOrderDTX().getChangeId() + AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        this.orderId = deserialize.getRecycleOrderDTX().getOrderId();
        this.orderVersion=deserialize.getRecycleOrderDTX().getOrderVersion();
        createBizStateMachineCommand = deserialize.getRecycleOrderDTX().getCreateBizStateMachineCommand();
    }

    public void markAsStarted() {
        this.cancelIncreaseOrderStorageLTXStatus = LTXStatus.STARTED;
        this.cancelUpdateOrderLTXStatus = LTXStatus.STARTED;
    }

    public void handle(CancelIncreaseOrderStorageForRecycleReplyEvent deserialize) {
        if(deserialize.isEmptyOpt())
            setCancelIncreaseOrderStorageLTXEmptyOpt(true);
        this.cancelIncreaseOrderStorageLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    public void handle(CancelUpdateOrderForRecycleReplyEvent deserialize) {
        if(deserialize.isEmptyOpt())
            setCancelUpdateOrderLTXEmptyOpt(true);
        this.cancelUpdateOrderLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    private void checkAll() {
        if (cancelIncreaseOrderStorageLTXStatus.equals(LTXStatus.SUCCESS) && cancelUpdateOrderLTXStatus.equals(LTXStatus.SUCCESS)) {
            DomainEventPublisher.instance().publish(new CancelRecycleOrderDTXSuccessEvent(this));
            this.setStatus(DTXStatus.SUCCESS);
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
