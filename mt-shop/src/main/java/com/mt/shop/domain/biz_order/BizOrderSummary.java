package com.mt.shop.domain.biz_order;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.shop.domain.biz_order.event.CreateBizOrderEvent;
import com.mt.shop.port.adapter.persistence.biz_order.BizOrderItemConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class BizOrderSummary extends Auditable {
    @Id
    private long id;

    @Embedded
    private BizOrderId orderId;
    @Embedded
    private ShippingDetail address;

    private String userId;

    @Convert(converter = BizOrderItemConverter.class)
    @Lob
    private List<CartDetail> productList;

    private String paymentType;

    private String paymentLink;

    private BigDecimal paymentAmt;

    private String paymentDate;
    @Setter
    private boolean paid;
    @Setter
    private boolean orderSku;
    @Setter
    private boolean actualSku;
    @Setter
    private boolean requestCancel;
    @Setter
    private Date modifiedByUserAt;

    public BizOrderSummary(CreateBizOrderEvent event) {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        orderId = event.getOrderId();
        address = event.getAddress();
        productList = event.getProductList();
        paymentType = event.getPaymentType().name();
        paymentLink = event.getPaymentLink();
        paymentAmt = event.getPaymentAmt();
        userId = event.getUserId();
        paymentDate = null;
        paid = false;
        actualSku = false;
        orderSku = true;
        requestCancel = false;
        modifiedByUserAt = event.getModifiedByUserAt();
    }

    public void update(ShippingDetail address) {
        this.address = address;
    }

    public BizOrderStatus getOrderState() {
        if(requestCancel){
            return BizOrderStatus.CANCELLED;
        }else{
            if(!paid&&orderSku&&!actualSku){
                return BizOrderStatus.NOT_PAID_RESERVED;
            }
            else if(!paid&&!orderSku&&!actualSku){
                return BizOrderStatus.NOT_PAID_RECYCLED;
            }
            else if(paid&&orderSku&&!actualSku){
                return BizOrderStatus.PAID_RESERVED;
            }
            else if(paid&&!orderSku&&!actualSku){
                return BizOrderStatus.PAID_RECYCLED;
            }
            else if(paid&&orderSku&&actualSku){
                return BizOrderStatus.CONFIRMED;
            }else{
                throw new IllegalStateException("unknown order state happened");
            }
        }
    }

    /**
     * hide order detailed state to customer
     * @return
     */
    public CustomerOrderStatus getOrderStateForCustomer() {
        if(!paid){
            return CustomerOrderStatus.DRAFT;
        }else{
            return CustomerOrderStatus.PAID;
        }
    }

    public enum CustomerOrderStatus {
        DRAFT,
        PAID;
    }
}
