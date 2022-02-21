package com.mt.shop.domain.biz_order;

import com.mt.common.domain.model.validate.Validator;
import com.mt.shop.application.biz_order.command.*;
import com.mt.shop.domain.biz_order.event.CreateBizOrderEvent;
import com.mt.shop.domain.biz_order.event.DeleteOrderEvent;
import com.mt.shop.domain.biz_order.event.UpdateBizOrderAddressEvent;
import com.mt.shop.domain.biz_order.event.UpdateBizOrderEvent;
import com.mt.shop.domain.biz_order.exception.BizOrderPaymentMismatchException;
import com.mt.shop.domain.biz_order.exception.BizOrderUpdateAddressAfterPaymentException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;


@Slf4j
@Aggregate
@Getter
public class BizOrder extends Auditable implements Serializable {
    private final boolean cancelled = false;
    @AggregateIdentifier
    private BizOrderId orderId;
    private ShippingDetail address;
    private UserDetail userDetail;
    private SkuDetail skuDetail;
    private LinkedHashSet<CartDetail> cartDetails;
    private PaymentDetail paymentDetail;

    public BizOrder() {
    }

    @CommandHandler
    public BizOrder(InternalCreateNewOrderCommand command) {
        log.debug("handling {}", command);
        apply(new CreateBizOrderEvent(command));
    }

    @CommandHandler
    public void handle(InternalCancelNewOrderEvent command) {
        log.debug("handling {}", command);
        if (version == 0
                && getUserDetail().getUserId().equalsIgnoreCase(command.getUserId())
        ) {
            //only new order that has never been updated can be delete automatically
            apply(new DeleteOrderEvent(command));
        } else {
            throw new IllegalStateException("cannot cancel order bcz it state has changed");
        }
    }

    @CommandHandler
    public void handle(InternalUpdateBizOrderCommand command) {
        log.debug("handling {}", command);
        if (command.getDeleted() == null) {
            if (!isDeleted()) {
                checkVersion(command.getVersion());
                apply(new UpdateBizOrderEvent(command));
            } else {
                throw new IllegalStateException("cannot update deleted order");
            }

        } else {
            //allow update for cancel operation
            checkVersion(command.getVersion());
            apply(new UpdateBizOrderEvent(command));

        }
    }

    //force to delete
    @CommandHandler
    public void handle(InternalAdminDeleteOrderEvent command) {
        log.debug("handling {}", command);
        if (!isDeleted()) {
            checkVersion(command.getVersion());
            Validator.notBlank(command.getUserId());
            apply(new DeleteOrderEvent(command));
        } else {
            throw new IllegalStateException("cannot update deleted order");
        }
    }

    @CommandHandler
    public void handle(CustomerUpdateBizOrderAddressCommand command) {
        checkVersion(command.getVersion());
        log.debug("handling {}", command);
        if (this.paymentDetail.getPaid() != PaymentDetail.PaymentStatus.UNPAID) {
            throw new BizOrderUpdateAddressAfterPaymentException();
        }
        apply(new UpdateBizOrderAddressEvent(command, this));
    }

    @EventSourcingHandler
    public void on(CreateBizOrderEvent event) {
        log.debug("applying {}", event);
        List<CartDetail> collect2 = event.getProductList();
        this.paymentDetail = new PaymentDetail(event.getPaymentType(), event.getPaymentAmt());
        this.userDetail = new UserDetail(event.getUserId(), event.getModifiedByUserAt());
        this.orderId = event.getOrderId();
        this.cartDetails = new LinkedHashSet<>(collect2);
        this.address = event.getAddress();
        this.skuDetail = SkuDetail.parse(cartDetails);
        validatePaymentAmount();
        this.version = 0;
    }

    @EventSourcingHandler
    public void on(DeleteOrderEvent event) {
        log.debug("applying {}", event);
        this.setDeleted(true);
        this.version = this.version + 1;
    }

    @EventSourcingHandler
    public void on(UpdateBizOrderAddressEvent event) {
        log.debug("applying {}", event);
        this.address = new ShippingDetail(event);
        this.userDetail.setModifiedByUserAt(event.getModifiedByUserAt());
        this.version = this.version + 1;
    }

    @EventSourcingHandler
    public void on(UpdateBizOrderEvent event) {
        log.debug("applying {}", event);
        Optional.ofNullable(event.getActualStorage()).ifPresent(e -> {
            this.skuDetail.setActualSkuStatus(e ? SkuDetail.SkuStatus.RESERVED : SkuDetail.SkuStatus.PENDING);
        });
        Optional.ofNullable(event.getOrderStorage()).ifPresent(e -> {
            this.skuDetail.setOrderSkuStatus(e ? SkuDetail.SkuStatus.RESERVED : SkuDetail.SkuStatus.PENDING);
        });
        Optional.ofNullable(event.getPaid()).ifPresent(e -> {
            this.paymentDetail.setPaid(e ? PaymentDetail.PaymentStatus.PAID : PaymentDetail.PaymentStatus.UNPAID);
        });
        Optional.ofNullable(event.getDeleted()).ifPresent(e -> {
            setDeleted(e);
            if (e) {
                setDeletedAt(event.getDeletedAt());
                setDeletedBy(event.getDeletedBy());
            } else {
                setDeletedBy(null);
                setDeletedAt(null);
            }
        });
        this.version = this.version + 1;
    }

    private void validatePaymentAmount() {
        BigDecimal reduce = cartDetails.stream().map(CartDetail::getFinalPrice).reduce(BigDecimal.valueOf(0), BigDecimal::add);
        if (this.paymentDetail.getPaymentAmt().compareTo(reduce) != 0)
            throw new BizOrderPaymentMismatchException();
    }
}

