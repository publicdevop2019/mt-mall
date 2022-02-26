package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.Validator;
import com.mt.shop.application.biz_order.command.CustomerPlaceBizOrderCommand;
import com.mt.shop.application.biz_order.command.CustomerUpdateBizOrderAddressCommand;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.biz_order.event.OrderOperationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BizOrderService {
    @Autowired
    private EntityManager entityManager;
    @Value("${order.expireAfter.milliseconds}")
    private Long expireAfter;

    public BizOrderId handlePlaceOrderAction(CustomerPlaceBizOrderCommand commandsProductList, String changeId) {
        log.debug("before submit order to saga");
        Validator.notEmpty(commandsProductList.getProductList(), "order must have at least one product");
        commandsProductList.getProductList().forEach((production) -> {
            Validator.notNull(production.getVersion(), "version must not be null");
        });
        BizOrderId bizOrderId = new BizOrderId();
        OrderOperationEvent event = new OrderOperationEvent();
        List<CartDetail> collect2 = getBizOrderItems(commandsProductList.getProductList());
        event.setOrderId(bizOrderId.getDomainId());
        event.setBizOrderEvent(BizOrderEvent.NEW_ORDER);
        event.setCreatedBy(UserThreadLocal.get());
        event.setUserId(UserThreadLocal.get());
        event.setOrderState(BizOrderStatus.DRAFT);
        event.setTxId(changeId);
        event.setPaymentAmt(commandsProductList.getPaymentAmt());
        event.setPaymentType(commandsProductList.getPaymentType());
        event.setAddress(commandsProductList.getAddress());
        event.setProductList(collect2);
        DomainEventPublisher.instance().publish(event);
        log.debug("order submitted to saga");
        return bizOrderId;
    }

    private List<CartDetail> getBizOrderItems(List<CustomerPlaceBizOrderCommand.BizOrderItemCommand> productList) {
        return productList.stream().map(CartDetail::new).collect(Collectors.toList());
    }

    public void confirmPayment(String orderId, String changeId) {
        BizOrderId bizOrderId = new BizOrderId(orderId);
        Optional<BizOrderSummary> bizOrder = DomainRegistry.getBizOderSummaryRepository().ordersOfQuery(new BizOrderQuery(bizOrderId, false)).findFirst();
        if (bizOrder.isPresent()) {
            BizOrderSummary rep = bizOrder.get();
            log.debug("start of confirmPayment");
            OrderOperationEvent event = new OrderOperationEvent();
            event.setOrderId(rep.getOrderId().getDomainId());
            event.setBizOrderEvent(BizOrderEvent.CONFIRM_PAYMENT);
            event.setCreatedBy(UserThreadLocal.get());
            event.setUserId(UserThreadLocal.get());
            event.setProductList(rep.getProductList());
            event.setOrderState(rep.getOrderState());
            event.setTxId(changeId);
            event.setVersion(rep.getVersion());
            DomainEventPublisher.instance().publish(event);
        } else {
            throw new IllegalArgumentException("invalid order id");
        }

    }

    public void reserveOrder(String id, String changeId) {
        Optional<BizOrderSummary> optionalBizOrder = DomainRegistry.getBizOderSummaryRepository()
                .ordersOfQuery(new BizOrderQuery(new BizOrderId(id), false)).findFirst();
        if (optionalBizOrder.isPresent()) {
            BizOrderSummary orderDetail = optionalBizOrder.get();
            OrderOperationEvent event = new OrderOperationEvent();
            event.setOrderId(orderDetail.getOrderId().getDomainId());
            event.setBizOrderEvent(BizOrderEvent.RESERVE);
            event.setCreatedBy(UserThreadLocal.get());
            event.setUserId(UserThreadLocal.get());
            event.setProductList(orderDetail.getProductList());
            event.setOrderState(orderDetail.getOrderState());
            event.setTxId(changeId);
            event.setVersion(orderDetail.getVersion());
            DomainEventPublisher.instance().publish(event);
        } else {
            throw new IllegalArgumentException("invalid order id");
        }
    }

    public void releaseExpiredOrder() {
        log.trace("start release expired orders");
        Date from = Date.from(Instant.ofEpochMilli(Instant.now().toEpochMilli() - expireAfter));
        List<BizOrderSummary> expiredOrderList = DomainRegistry.getBizOderSummaryRepository().findExpiredNotPaidReserved(from);
        if (!expiredOrderList.isEmpty()) {
            //only send first 5 orders due to saga pool size is 100
            expiredOrderList.stream().limit(5).collect(Collectors.toList()).forEach(expiredOrder -> {
                OrderOperationEvent event = new OrderOperationEvent();
                event.setTxId(UUID.randomUUID().toString());
                event.setOrderId(expiredOrder.getOrderId().getDomainId());
                event.setOrderState(expiredOrder.getOrderState());
                event.setBizOrderEvent(BizOrderEvent.RECYCLE_ORDER_STORAGE);
                event.setVersion(expiredOrder.getVersion());
                event.setProductList(expiredOrder.getProductList());

                DomainEventPublisher.instance().publish(event);
            });
            log.info("expired order(s) found");
        }
    }

    public void concludeOrder() {
        List<BizOrderSummary> paidReserved = DomainRegistry.getBizOderSummaryRepository().findPaidReservedDraft();
        if (!paidReserved.isEmpty()) {
            log.info("paid reserved order(s) found {}", paidReserved.stream().map(BizOrderSummary::getOrderId).collect(Collectors.toList()));
            //only send first order due to saga pool size
            paidReserved.stream().limit(1).forEach(order -> {
                OrderOperationEvent event = new OrderOperationEvent();
                event.setTxId(UUID.randomUUID().toString());
                event.setOrderId(order.getOrderId().getDomainId());
                event.setOrderState(order.getOrderState());
                event.setBizOrderEvent(BizOrderEvent.CONFIRM_ORDER);
                event.setProductList(order.getProductList());
                event.setVersion(order.getVersion());
                DomainEventPublisher.instance().publish(event);
            });
        }
    }

    public void resubmitOrder() {
        List<BizOrderSummary> paidRecycled = DomainRegistry.getBizOderSummaryRepository().findPaidRecycled();
        if (!paidRecycled.isEmpty()) {
            log.info("paid recycled order(s) found {}", paidRecycled.stream().map(BizOrderSummary::getOrderId).collect(Collectors.toList()));
            paidRecycled.forEach(order -> {
                OrderOperationEvent event = new OrderOperationEvent();
                event.setTxId(UUID.randomUUID().toString());
                event.setOrderId(order.getOrderId().getDomainId());
                event.setOrderState(order.getOrderState());
                event.setBizOrderEvent(BizOrderEvent.RESERVE);
                event.setVersion(order.getVersion());
                event.setProductList(order.getProductList());
                DomainEventPublisher.instance().publish(event);
            });
        }
    }

    public void updateAddress(String id, String changeId, CustomerUpdateBizOrderAddressCommand command) {
        BizOrderId bizOrderId = new BizOrderId(id);
        Optional<BizOrderSummary> bizOrder = DomainRegistry.getBizOderSummaryRepository().ordersOfQuery(new BizOrderQuery(bizOrderId, false)).findFirst();
        if (bizOrder.isPresent()) {
            if (!bizOrder.get().isPaid() && !bizOrder.get().isRequestCancel()) {
                BizOrderSummary rep = bizOrder.get();
                log.debug("start of update address");
                OrderOperationEvent event = new OrderOperationEvent();
                event.setOrderId(rep.getOrderId().getDomainId());
                event.setBizOrderEvent(BizOrderEvent.UPDATE_ADDRESS);
                event.setCreatedBy(UserThreadLocal.get());
                event.setUserId(UserThreadLocal.get());
                event.setOrderState(rep.getOrderState());
                event.setTxId(changeId);
                event.setVersion(rep.getVersion());
                event.setAddress(new CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep(command));
                event.setOriginalAddress(rep.getAddress());
                event.setOriginalModifiedByUserAt(rep.getModifiedByUserAt().getTime());
                DomainEventPublisher.instance().publish(event);
            } else {
                throw new IllegalStateException("cannot update address of paid or cancelled request");
            }
        } else {
            throw new IllegalArgumentException("invalid order id " + id);
        }
    }

    public void userCancelOrder(String id, String changeId) {
        BizOrderId bizOrderId = new BizOrderId(id);
        Optional<BizOrderSummary> bizOrder = DomainRegistry.getBizOderSummaryRepository().ordersOfQuery(new BizOrderQuery(bizOrderId, false)).findFirst();
        if (bizOrder.isPresent()) {
            if (!bizOrder.get().isRequestCancel()) {
                BizOrderSummary rep = bizOrder.get();
                log.debug("start of cancel order");
                OrderOperationEvent event = new OrderOperationEvent();
                event.setOrderId(rep.getOrderId().getDomainId());
                event.setBizOrderEvent(BizOrderEvent.CANCEL_ORDER);
                event.setCreatedBy(UserThreadLocal.get());
                event.setUserId(UserThreadLocal.get());
                event.setOrderState(rep.getOrderState());
                event.setTxId(changeId);
                event.setProductList(rep.getProductList());
                event.setVersion(rep.getVersion());
                DomainEventPublisher.instance().publish(event);
            } else {
                throw new IllegalStateException("cannot re-cancel order");
            }
        } else {
            throw new IllegalArgumentException("invalid order id");
        }
    }
}
