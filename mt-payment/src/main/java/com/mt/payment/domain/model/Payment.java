package com.mt.payment.domain.model;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "Payment")
@Data
@NoArgsConstructor
public class Payment extends Auditable {
    private static final long serialVersionUID = 1;
    @Id
    Long id;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String userId;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String orderId;
    /**
     * @note from wechat
     */
    private String prepayId;

    private PaymentStatus status;

    public Payment(String userId, String orderId) {
        String prepayId = callWeChatPaymentAPI();
        setPrepayId(prepayId);
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.userId = userId;
        this.orderId = orderId;
        this.status = PaymentStatus.UNPAID;
        notifyWeChatToStartUserPay(prepayId);
    }

    private static String callWeChatPaymentAPI() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static void notifyWeChatToStartUserPay(String prepayId) {
    }

    public void cancel() {
        status = PaymentStatus.CANCELLED;
    }
    public void refund() {
        status = PaymentStatus.UNPAID;
    }
    public void charge() {
        status = PaymentStatus.PAID;
    }
}
