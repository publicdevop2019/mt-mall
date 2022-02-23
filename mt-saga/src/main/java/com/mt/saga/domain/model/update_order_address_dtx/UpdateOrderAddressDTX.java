package com.mt.saga.domain.model.update_order_address_dtx;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.appliction.update_order_address_dtx.command.UpdateOrderAddressSuccessReplyEvent;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import com.mt.saga.domain.model.order_state_machine.event.CreateUpdateOrderAddressDTXEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderAddressDTXFailedEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderAddressDTXSuccessEvent;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderForUpdateOrderAddressEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table
@Data
@NoArgsConstructor
public class UpdateOrderAddressDTX extends Auditable implements Serializable {
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
    @Column(length = 25)
    @Convert(converter = BizOrderStatus.DBConverter.class)
    private BizOrderStatus orderStatus;
    private Integer orderVersion;
    @Lob
    private String orderCommandDetail;

    @Convert(converter = LTXStatus.DBConverter.class)
    private LTXStatus updateOrderLTXStatus = LTXStatus.PENDING;
    private boolean updateOrderLTXEmptyOpt=false;

    public UpdateOrderAddressDTX(CreateUpdateOrderAddressDTXEvent event) {
        CommonOrderCommand command = event.getCommand();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.status = DTXStatus.STARTED;
        this.changeId = command.getTxId();
        this.orderId = command.getOrderId();
        this.orderStatus=command.getOrderState();
        setOrderVersion(command.getVersion());
        orderCommandDetail = CommonDomainRegistry.getCustomObjectSerializer().serialize(command);
    }

    public void markAsStarted() {
        updateOrderLTXStatus = LTXStatus.STARTED;
    }

    private void checkAll() {
        if (updateOrderLTXStatus.equals(LTXStatus.SUCCESS)) {
            DomainRegistry.getIsolationService().removeActiveDtx(orderId);
            DomainEventPublisher.instance().publish(new UpdateOrderAddressDTXSuccessEvent(this));
            this.status = DTXStatus.SUCCESS;
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
                    if(UpdateOrderForUpdateOrderAddressEvent.name.equals(event.getName())){
                        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(event.getId());
                    }
                }
            });
        }
    }

    public void cancel() {
            DomainEventPublisher.instance().publish(new UpdateOrderAddressDTXFailedEvent(this));
//        if (getStatus().equals(DTXStatus.STARTED)) {
//        } else {
//            throw new IllegalStateException("can cancel started dtx only");
//        }
    }

    public void updateStatus(UpdateOrderAddressSuccessReplyEvent event) {
        if(event.isEmptyOpt())
            this.updateOrderLTXEmptyOpt=true;
        this.updateOrderLTXStatus = LTXStatus.SUCCESS;
        checkAll();
    }
}
