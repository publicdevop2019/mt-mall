package com.mt.saga.domain.model.cancel_update_order_address_dtx;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.command.CancelUpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.appliction.cancel_update_order_address_dtx.command.CancelUpdateOrderAddressReplyEvent;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderAddressDTXSuccessEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXFailedEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderAddressDTXFailedEvent;
import com.mt.saga.infrastructure.AppConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor
public class CancelUpdateOrderAddressDTX extends Auditable {
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

    public CancelUpdateOrderAddressDTX(UpdateOrderAddressDTXFailedEvent event) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.forwardDtxId = event.getDtx().getId();
        this.changeId = event.getDtx().getChangeId() + AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        this.orderId = event.getDtx().getOrderId();
        this.createBizStateMachineCommand = event.getDtx().getOrderCommandDetail();
        setOrderVersion(event.getDtx().getOrderVersion());
    }


    public void markAsStarted() {
        this.status = DTXStatus.STARTED;
        this.cancelUpdateOrderLTXStatus = LTXStatus.STARTED;
    }

    public void updateStatus(CancelUpdateOrderAddressReplyEvent event) {
        if(event.isEmptyOpt()){
            setCancelUpdateOrderLTXEmptyOpt(true);
        }
        setCancelUpdateOrderLTXStatus(LTXStatus.SUCCESS);
        setStatus(DTXStatus.SUCCESS);
        DomainEventPublisher.instance().publish(new CancelUpdateOrderAddressDTXSuccessEvent(this));
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
