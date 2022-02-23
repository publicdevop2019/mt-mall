package com.mt.saga.domain;

import com.mt.saga.appliction.cancel_create_order_dtx.CancelCreateOrderDTXApplicationService;
import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTXRepository;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTXRepository;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTXRepository;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTXRepository;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTXRepository;
import com.mt.saga.domain.model.distributed_tx.DistributedTxRepository;
import com.mt.saga.domain.model.order_state_machine.OrderStateMachineBuilder;
import com.mt.saga.domain.model.order_state_machine.PaymentService;
import com.mt.saga.domain.model.order_state_machine.ProductService;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTXRepository;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTXRepository;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTXRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static OrderStateMachineBuilder orderStateMachineBuilder;
    @Getter
    private static IsolationService isolationService;
    @Getter
    private static RecycleOrderDTXRepository recycleOrderDTXRepository;
    @Getter
    private static ReserveOrderDTXRepository reserveOrderDTXRepository;
    @Getter
    private static ConfirmOrderPaymentDTXRepository confirmOrderPaymentDTXRepository;
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
    private static CancelRecycleOrderDTXRepository cancelRecycleOrderDTXRepository;
    @Getter
    private static PostDTXValidationService postDtxValidationService;
    @Getter
    private static DistributedTxRepository distributedTxRepository;
    @Getter
    private static CancelUpdateOrderAddressDTXRepository cancelUpdateOrderAddressDTXRepository;
    @Getter
    private static UpdateOrderAddressDTXRepository updateOrderAddressDTXRepository;

    @Autowired
    private void setDistributedTxRepository(DistributedTxRepository distributedTxRepository) {
        DomainRegistry.distributedTxRepository = distributedTxRepository;
    }


    @Autowired
    private void setIsolationService(IsolationService isolationService) {
        DomainRegistry.isolationService = isolationService;
    }

    @Autowired
    private void setCancelUpdateOrderAddressDTXRepository(CancelUpdateOrderAddressDTXRepository cancelUpdateOrderAddressDTXRepository) {
        DomainRegistry.cancelUpdateOrderAddressDTXRepository = cancelUpdateOrderAddressDTXRepository;
    }

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
    private void setProductService(ProductService productService) {
        DomainRegistry.productService = productService;
    }


    @Autowired
    private void setPaymentService(PaymentService paymentService) {
        DomainRegistry.paymentService = paymentService;
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

}
