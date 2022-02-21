package com.mt.shop.domain.biz_order;

import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.biz_order.event.CreateBizOrderEvent;
import com.mt.shop.domain.biz_order.event.DeleteOrderEvent;
import com.mt.shop.domain.biz_order.event.UpdateBizOrderAddressEvent;
import com.mt.shop.domain.biz_order.event.UpdateBizOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Component
public class BizOrderSummaryProjection {
    private final EntityManager entityManager;
    private final QueryUpdateEmitter queryUpdateEmitter;

    public BizOrderSummaryProjection(EntityManager entityManager, QueryUpdateEmitter queryUpdateEmitter) {
        this.entityManager = entityManager;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    @Transactional
    public void on(CreateBizOrderEvent event) {
        log.debug("projecting {}", event);
        BizOrderSummary bizOrderSummary = new BizOrderSummary(event);
        DomainRegistry.getBizOderSummaryRepository().add(bizOrderSummary);
    }

    @EventHandler
    @Transactional
    public void on(DeleteOrderEvent event) {
        log.debug("projecting {}", event);
        Optional<BizOrderSummary> bizOrderSummary = DomainRegistry.getBizOderSummaryRepository()
                .ordersOfQuery(new BizOrderQuery(event.getOrderId(), true)).findFirst();
        bizOrderSummary.ifPresentOrElse(order -> {
            order.setDeleted(true);
        }, () -> log.error("unable to find object"));
    }

    @EventHandler
    @Transactional
    public void on(UpdateBizOrderEvent event) {
        log.debug("projecting {}", event);

        Optional<BizOrderSummary> bizOrderSummary = DomainRegistry.getBizOderSummaryRepository()
                .nativeOrdersOfQuery(new BizOrderQuery(event.getOrderId(), true)).findFirst();
        bizOrderSummary.ifPresentOrElse(order -> {
            Optional.ofNullable(event.getActualStorage()).ifPresent(order::setActualSku);
            Optional.ofNullable(event.getOrderStorage()).ifPresent(order::setOrderSku);
            Optional.ofNullable(event.getPaid()).ifPresent(order::setPaid);
            Optional.ofNullable(event.getDeleted()).ifPresent(e -> {
                order.setDeleted(e);
                if (e) {
                    order.setDeletedAt(event.getDeletedAt());
                    order.setDeletedBy(event.getDeletedBy());
                } else {
                    order.setDeletedAt(null);
                    order.setDeletedBy(null);
                }
            });
        }, () -> log.error("unable to find object"));
    }


    @EventHandler
    @Transactional
    public void on(UpdateBizOrderAddressEvent event) {
        log.debug("projecting {}", event);
        Optional<BizOrderSummary> bizOrderSummary = DomainRegistry.getBizOderSummaryRepository().
                ordersOfQuery(new BizOrderQuery(event.getBizOrderId(), true)).findFirst();
        bizOrderSummary.ifPresentOrElse(e -> {
            e.update(new ShippingDetail(event));
            e.setModifiedByUserAt(event.getModifiedByUserAt());
        }, () -> log.error("unable to find object"));
    }

}
