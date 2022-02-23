package com.mt.saga.domain;

import com.mt.saga.domain.model.IsolationService;
import com.mt.saga.domain.model.distributed_tx.DistributedTxRepository;
import com.mt.saga.domain.model.order_state_machine.OrderStateMachineBuilder;
import com.mt.saga.domain.model.order_state_machine.PaymentService;
import com.mt.saga.domain.model.order_state_machine.ProductService;
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
    private static PaymentService paymentService;
    @Getter
    private static ProductService productService;
    @Getter
    private static PostDTXValidationService postDtxValidationService;
    @Getter
    private static DistributedTxRepository distributedTxRepository;

    @Autowired
    private void setDistributedTxRepository(DistributedTxRepository distributedTxRepository) {
        DomainRegistry.distributedTxRepository = distributedTxRepository;
    }


    @Autowired
    private void setIsolationService(IsolationService isolationService) {
        DomainRegistry.isolationService = isolationService;
    }

    @Autowired
    private void setPostDtxValidationService(PostDTXValidationService postDtxValidationService) {
        DomainRegistry.postDtxValidationService = postDtxValidationService;
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
    private void setOrderStateMachineBuilder(OrderStateMachineBuilder orderStateMachineBuilder) {
        DomainRegistry.orderStateMachineBuilder = orderStateMachineBuilder;
    }

}
