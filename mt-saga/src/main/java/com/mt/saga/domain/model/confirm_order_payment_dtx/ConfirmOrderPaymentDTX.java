package com.mt.saga.domain.model.confirm_order_payment_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.confirm_order_payment_dtx.command.UpdateOrderPaymentSuccessReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXFailedEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.UpdateOrderPaymentSuccessEvent;
import com.mt.saga.domain.model.order_state_machine.event.CreateConfirmOrderPaymentDTXEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Data
@NoArgsConstructor
@Slf4j
public class ConfirmOrderPaymentDTX extends Auditable implements Serializable {
    @Id
    private Long id;

    @Column(length = 25)
    @Convert(converter = DTXStatus.DBConverter.class)
    private DTXStatus status = DTXStatus.PENDING;

    private String changeId;
    private String orderId;
    private Integer orderVersion;
    private BizOrderStatus bizOrderStatus;
    @Lob
    private String createBizStateMachineCommand;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus updateOrderLTXStatus = LTXStatus.PENDING;
    private boolean updateOrderLTXEmptyOpt = false;

    public ConfirmOrderPaymentDTX(CreateConfirmOrderPaymentDTXEvent event) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.STARTED;
        this.changeId = event.getCommand().getTxId();
        this.orderId = event.getCommand().getOrderId();
        this.orderVersion = event.getCommand().getVersion();
        this.bizOrderStatus = event.getCommand().getOrderState();
        createBizStateMachineCommand = CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getCommand());
    }


    public void markAsStarted() {
        this.status = DTXStatus.STARTED;
        this.updateOrderLTXStatus = LTXStatus.STARTED;
    }

    public void updateStatus(UpdateOrderPaymentSuccessReplyEvent deserialize) {
        if(deserialize.isEmptyOpt())
            this.updateOrderLTXEmptyOpt=true;
        this.setUpdateOrderLTXStatus(LTXStatus.SUCCESS);
        setStatus(DTXStatus.SUCCESS);
        DomainRegistry.getIsolationService().removeActiveDtx(orderId);

        log.debug("previous order status is {}",bizOrderStatus);
        if(bizOrderStatus.equals(BizOrderStatus.NOT_PAID_RESERVED)){
            //auto trigger order conclude
            DomainEventPublisher.instance().publish(new ConfirmOrderPaymentDTXSuccessEvent(this));
        }
    }

    public void retryStartedLtx() {
        if(getStatus().equals(DTXStatus.STARTED)){
            SumPagedRep<StoredEvent> relatedEvents = CommonDomainRegistry.getEventRepository().query(
                    new StoredEventQuery("domainId:" + getId(),
                            PageConfig.defaultConfig().getRawValue()
                            , QueryConfig.skipCount().value()));

            relatedEvents.getData().forEach(event->{
                if(getUpdateOrderLTXStatus().equals(LTXStatus.STARTED)){
                    if(UpdateOrderPaymentSuccessEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
            });
        }
    }

    public void cancel() {
        if (getStatus().equals(DTXStatus.STARTED)) {
        DomainEventPublisher.instance().publish(new ConfirmOrderPaymentDTXFailedEvent(this));
        } else {
            throw new IllegalStateException("can cancel started dtx only");
        }
    }
}
