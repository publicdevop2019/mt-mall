package com.mt.saga.domain.model.cancel_conclude_order_dtx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.cancel_conclude_order_dtx.command.CancelDecreaseActualStorageForConcludeReplyEvent;
import com.mt.saga.appliction.cancel_conclude_order_dtx.command.CancelUpdateOrderForConcludeReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@NoArgsConstructor
public class CancelConcludeOrderDTX extends Auditable implements Serializable {
    public static final String ENTITY_TX_STATUS = "txStatus";
    public static final String ENTITY_REFERENCE_ID = "referenceId";
    @Id
    private Long id;
    private Long forwardDtxId;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status = DTXStatus.PENDING;

    private String changeId;
    private String orderId;
    private String resolveReason;
    private Integer orderVersion;

    @Lob
    private String createBizStateMachineCommand;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelDecreaseActualStorageLTXStatus = LTXStatus.PENDING;
    private boolean cancelDecreaseActualStorageLTXEmptyOpt = false;
    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelUpdateOrderLTXStatus = LTXStatus.PENDING;
    private boolean cancelUpdateOrderLTXEmptyOpt = false;

    public CancelConcludeOrderDTX(ConcludeOrderDTXFailedEvent deserialize) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.changeId = deserialize.getConcludeOrderDTX().getChangeId() + AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        this.orderId = deserialize.getConcludeOrderDTX().getOrderId();
        createBizStateMachineCommand = deserialize.getConcludeOrderDTX().getCreateBizStateMachineCommand();
        setOrderVersion(deserialize.getConcludeOrderDTX().getOrderVersion());
        setForwardDtxId(deserialize.getConcludeOrderDTX().getId());
    }

    public void handle(CancelUpdateOrderForConcludeReplyEvent event) {
        if(event.isEmptyOpt()){
            setCancelUpdateOrderLTXEmptyOpt(true);
        }
        this.cancelUpdateOrderLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    public void handle(CancelDecreaseActualStorageForConcludeReplyEvent event) {
        if(event.isEmptyOpt()){
            setCancelDecreaseActualStorageLTXEmptyOpt(true);
        }
        this.cancelDecreaseActualStorageLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }

    private void checkAll() {
        if (cancelDecreaseActualStorageLTXStatus.equals(LTXStatus.SUCCESS) && cancelUpdateOrderLTXStatus.equals(LTXStatus.SUCCESS)) {
            this.setStatus(DTXStatus.SUCCESS);
            DomainEventPublisher.instance().publish(new CancelConcludeOrderDTXSuccessEvent(this));
        }
    }

    public void markAsStarted() {
        this.cancelDecreaseActualStorageLTXStatus = LTXStatus.STARTED;
        this.cancelUpdateOrderLTXStatus = LTXStatus.STARTED;
        this.status=DTXStatus.STARTED;
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
