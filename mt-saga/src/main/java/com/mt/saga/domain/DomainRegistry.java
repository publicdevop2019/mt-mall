package com.mt.saga.domain;

import com.mt.saga.appliction.cancel_create_order_dtx.CancelCreateOrderDTXApplicationService;
import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXRepository;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTXRepository;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTXRepository;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTXRepository;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTXRepository;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTXRepository;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTXRepository;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTXRepository;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTXRepository;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTXRepository;
import com.mt.saga.domain.model.distributed_tx.DistributedTxRepository;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTXRepository;
import com.mt.saga.domain.model.order_state_machine.OrderStateMachineBuilder;
import com.mt.saga.domain.model.order_state_machine.PaymentService;
import com.mt.saga.domain.model.order_state_machine.ProductService;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTXRepository;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTXRepository;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTXRepository;
import com.mt.saga.port.adapter.persistence.SpringDataJpaCreateOrderDTXRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static OrderStateMachineBuilder orderStateMachineBuilder;
    @Getter
    private static CreateOrderDTXRepository createOrderDTXRepository;
    @Getter
    private static IsolationService isolationService;
    @Getter
    private static CancelCreateOrderDTXRepository cancelCreateOrderDTXRepository;
    @Getter
    private static RecycleOrderDTXRepository recycleOrderDTXRepository;
    @Getter
    private static ReserveOrderDTXRepository reserveOrderDTXRepository;
    @Getter
    private static ConfirmOrderPaymentDTXRepository confirmOrderPaymentDTXRepository;
    @Getter
    private static ConcludeOrderDTXRepository concludeOrderDTXRepository;
    @Getter
    private static PaymentService paymentService;
    @Getter
    private static ProductService productService;
    @Getter
    private static CancelCreateOrderDTXApplicationService cancelCreateOrderDTXApplicationService;
    @Getter
    private static CancelConfirmOrderPaymentDTXRepository cancelConfirmOrderPaymentDTXRepository;
    @Getter
    private static CancelReserveOrderDTXRepository cancelReserveOrderDTXRepository;
    @Getter
    private static CancelConcludeOrderDTXRepository cancelConcludeOrderDTXRepository;
    @Getter
    private static CancelRecycleOrderDTXRepository cancelRecycleOrderDTXRepository;
    @Getter
    private static PostDTXValidationService postDtxValidationService;
    @Getter
    private static InvalidOrderDTXRepository invalidOrderDTXRepository;
    @Getter
    private static CancelInvalidOrderDTXRepository cancelInvalidOrderDTXRepository;
    @Getter
    private static DistributedTxRepository distributedTxRepository;

    @Autowired
    private void setDistributedTxRepository(DistributedTxRepository distributedTxRepository) {
        DomainRegistry.distributedTxRepository = distributedTxRepository;
    }
    @Autowired
    private void setCancelInvalidOrderDTXRepository(CancelInvalidOrderDTXRepository cancelInvalidOrderDTXRepository) {
        DomainRegistry.cancelInvalidOrderDTXRepository = cancelInvalidOrderDTXRepository;
    }
    @Autowired
    private void setInvalidOrderDTXRepository(InvalidOrderDTXRepository invalidOrderDTXRepository) {
        DomainRegistry.invalidOrderDTXRepository = invalidOrderDTXRepository;
    }
    @Autowired
    private void setIsolationService(IsolationService isolationService) {
        DomainRegistry.isolationService = isolationService;
    }
    @Getter
    private static CancelUpdateOrderAddressDTXRepository cancelUpdateOrderAddressDTXRepository;

    @Autowired
    private void setCancelUpdateOrderAddressDTXRepository(CancelUpdateOrderAddressDTXRepository cancelUpdateOrderAddressDTXRepository) {
        DomainRegistry.cancelUpdateOrderAddressDTXRepository = cancelUpdateOrderAddressDTXRepository;
    }
    @Getter
    private static UpdateOrderAddressDTXRepository updateOrderAddressDTXRepository;

    @Autowired
    private void setUpdateOrderAddressDTXRepository(UpdateOrderAddressDTXRepository updateOrderAddressDTXRepository) {
        DomainRegistry.updateOrderAddressDTXRepository = updateOrderAddressDTXRepository;
    }

    @Autowired
    private void setPostDtxValidationService(PostDTXValidationService postDtxValidationService) {
        DomainRegistry.postDtxValidationService = postDtxValidationService;
    }

    @Autowired
    private void setCancelRecycleOrderDTXRepository(CancelRecycleOrderDTXRepository cancelRecycleOrderDTXRepository) {
        DomainRegistry.cancelRecycleOrderDTXRepository = cancelRecycleOrderDTXRepository;
    }

    @Autowired
    private void setCancelConcludeOrderDTXRepository(CancelConcludeOrderDTXRepository cancelConcludeOrderDTXRepository) {
        DomainRegistry.cancelConcludeOrderDTXRepository = cancelConcludeOrderDTXRepository;
    }

    @Autowired
    private void setCancelReserveOrderDTXRepository(CancelReserveOrderDTXRepository cancelReserveOrderDTXRepository) {
        DomainRegistry.cancelReserveOrderDTXRepository = cancelReserveOrderDTXRepository;
    }

    @Autowired
    private void setCancelConfirmOrderPaymentDTXRepository(CancelConfirmOrderPaymentDTXRepository cancelConfirmOrderPaymentDTXRepository) {
        DomainRegistry.cancelConfirmOrderPaymentDTXRepository = cancelConfirmOrderPaymentDTXRepository;
    }

    @Autowired
    private void setCancelCreateOrderDTXApplicationService(CancelCreateOrderDTXApplicationService cancelCreateOrderDTXApplicationService) {
        DomainRegistry.cancelCreateOrderDTXApplicationService = cancelCreateOrderDTXApplicationService;
    }

    @Autowired
    private void setCancelCreateOrderDTXRepository(CancelCreateOrderDTXRepository cancelCreateOrderDTXRepository) {
        DomainRegistry.cancelCreateOrderDTXRepository = cancelCreateOrderDTXRepository;
    }

    @Autowired
    private void setProductService(ProductService productService) {
        DomainRegistry.productService = productService;
    }


    @Autowired
    private void setPaymentService(PaymentService paymentService) {
        DomainRegistry.paymentService = paymentService;
    }

    @Autowired
    private void setConcludeOrderDTXRepository(ConcludeOrderDTXRepository concludeOrderDTXRepository) {
        DomainRegistry.concludeOrderDTXRepository = concludeOrderDTXRepository;
    }


    @Autowired
    private void setReserveOrderDTXRepository(ReserveOrderDTXRepository reserveOrderDTXRepository) {
        DomainRegistry.reserveOrderDTXRepository = reserveOrderDTXRepository;
    }

    @Autowired
    private void setConfirmOrderPaymentDTXRepository(ConfirmOrderPaymentDTXRepository confirmOrderPaymentDTXRepository) {
        DomainRegistry.confirmOrderPaymentDTXRepository = confirmOrderPaymentDTXRepository;
    }

    @Autowired
    private void setRecycleOrderDTXRepository(RecycleOrderDTXRepository recycleOrderDTXRepository) {
        DomainRegistry.recycleOrderDTXRepository = recycleOrderDTXRepository;
    }

    @Autowired
    private void setOrderStateMachineBuilder(OrderStateMachineBuilder orderStateMachineBuilder) {
        DomainRegistry.orderStateMachineBuilder = orderStateMachineBuilder;
    }

    @Autowired
    private void setCreateOrderDTXRepository(SpringDataJpaCreateOrderDTXRepository createOrderTaskRepository) {
        DomainRegistry.createOrderDTXRepository = createOrderTaskRepository;
    }
}
