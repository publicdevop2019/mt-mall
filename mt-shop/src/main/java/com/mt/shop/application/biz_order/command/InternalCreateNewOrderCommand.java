package com.mt.shop.application.biz_order.command;

import com.mt.shop.domain.model.biz_order.BizOrderId;
import com.mt.shop.domain.model.biz_order.BizOrderItemAddOn;
import com.mt.shop.domain.model.biz_order.PaymentDetail;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
public class InternalCreateNewOrderCommand implements Serializable {
    private static final long serialVersionUID = 1;
    @TargetAggregateIdentifier
    private BizOrderId orderId;
    private String userId;
    private long taskId;
    private String createdBy;
    private CustomerPlaceBizOrderCommand.BizOrderAddressCmdRep address;
    private List<CartDetailCommand> productList;
    private PaymentDetail.PaymentType paymentType;
    private BigDecimal paymentAmt;
    private String paymentLink;
    private String changeId;

    @Data
    public static class CartDetailCommand {
        private String name;
        private List<BizOrderItemAddOn> selectedOptions;
        private BigDecimal finalPrice;
        private String productId;
        private String skuId;
        private String cartId;
        private Integer amount;
        private Set<String> attributesSales;
        private String imageUrlSmall;
        private Integer version;
        private HashMap<String, String> attrIdMap;

    }
}
