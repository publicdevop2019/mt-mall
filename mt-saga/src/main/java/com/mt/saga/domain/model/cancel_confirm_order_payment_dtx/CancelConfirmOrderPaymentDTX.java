package com.mt.saga.domain.model.cancel_confirm_order_payment_dtx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command.CancelUpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@NoArgsConstructor
public class CancelConfirmOrderPaymentDTX extends Auditable implements Serializable {
    @Id
    private Long id;
    private Long forwardDtxId;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status = DTXStatus.PENDING;

    private String changeId;
    private String orderId;
    private Integer orderVersion;
    private String resolveReason;

    @Lob
    private String createBizStateMachineCommand;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus cancelUpdateOrderLTXStatus = LTXStatus.PENDING;
    private boolean cancelUpdateOrderLTXEmptyOpt = false;

    public CancelConfirmOrderPaymentDTX(ConfirmOrderPaymentDTXFailedEvent event) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.forwardDtxId = event.getConfirmOrderPaymentDTX().getId();
        this.changeId = event.getConfirmOrderPaymentDTX().getChangeId() + AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        this.orderId = event.getConfirmOrderPaymentDTX().getOrderId();
        this.createBizStateMachineCommand = event.getConfirmOrderPaymentDTX().getCreateBizStateMachineCommand();
        setOrderVersion(event.getConfirmOrderPaymentDTX().getOrderVersion());
    }


    public void markAsStarted() {
        this.status = DTXStatus.STARTED;
        this.cancelUpdateOrderLTXStatus = LTXStatus.STARTED;
    }

    public void updateStatus(CancelUpdateOrderPaymentSuccessReplyEvent event) {
        if(event.isEmptyOpt()){
            setCancelUpdateOrderLTXEmptyOpt(true);
        }
        setCancelUpdateOrderLTXStatus(LTXStatus.SUCCESS);
        setStatus(DTXStatus.SUCCESS);
        DomainEventPublisher.instance().publish(new CancelConfirmOrderPaymentDTXSuccessEvent(this));
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
