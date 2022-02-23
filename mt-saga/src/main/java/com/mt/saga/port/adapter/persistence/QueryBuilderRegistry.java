package com.mt.saga.port.adapter.persistence;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaDistributedTxRepository.JpaCriteriaApiDistributedTxAdapter distributedTxAdapter;
    @Getter
    private static SpringDataJpaCancelConfirmOrderPaymentDTXRepository.JpaCriteriaApiCancelConfirmOrderPaymentDTXAdapter cancelConfirmOrderPaymentDTXAdapter;
    @Getter
    private static SpringDataJpaCancelRecycleOrderDTXRepository.JpaCriteriaApiCancelRecycleOrderDTXAdapter cancelRecycleOrderDTXAdapter;
    @Getter
    private static SpringDataJpaCancelReserveOrderDTXRepository.JpaCriteriaApiCancelReserveOrderDTXAdapter cancelReserveOrderDTXAdapter;
    @Getter
    private static SpringDataJpaConfirmOrderPaymentDTXRepository.JpaCriteriaApiConfirmOrderPaymentDTXAdapter confirmOrderPaymentDTXAdapter;
    @Getter
    private static SpringDataJpaRecycleOrderDTXRepository.JpaCriteriaApiRecycleOrderDTXAdapter recycleOrderDTXAdapter;
    @Getter
    private static SpringDataJpaReserveOrderDTXRepository.JpaCriteriaApiReserveOrderDTXAdapter reserveOrderDTXAdapter;
    @Getter
    private static SpringDataJpaUpdateOrderAddressDTXRepository.JpaCriteriaApiUpdateOrderAddressDTXAdapter updateOrderAddressDTXAdapter;
    @Getter
    private static SpringDataJpaCancelUpdateOrderAddressDTXRepository.JpaCriteriaApiCancelUpdateOrderAddressDTXAdapter cancelUpdateOrderAddressDTXAdapter;

    @Autowired
    public void setDistributedTxAdapter(SpringDataJpaDistributedTxRepository.JpaCriteriaApiDistributedTxAdapter distributedTxAdapter) {
        QueryBuilderRegistry.distributedTxAdapter = distributedTxAdapter;
    }

    @Autowired
    public void setCancelUpdateOrderAddressDTXAdapter(SpringDataJpaCancelUpdateOrderAddressDTXRepository.JpaCriteriaApiCancelUpdateOrderAddressDTXAdapter cancelUpdateOrderAddressDTXAdapter) {
        QueryBuilderRegistry.cancelUpdateOrderAddressDTXAdapter = cancelUpdateOrderAddressDTXAdapter;
    }

    @Autowired
    public void setUpdateOrderAddressDTXAdapter(SpringDataJpaUpdateOrderAddressDTXRepository.JpaCriteriaApiUpdateOrderAddressDTXAdapter updateOrderAddressDTXAdapter) {
        QueryBuilderRegistry.updateOrderAddressDTXAdapter = updateOrderAddressDTXAdapter;
    }


    @Autowired
    public void setCancelConfirmOrderPaymentDTXAdapter(SpringDataJpaCancelConfirmOrderPaymentDTXRepository.JpaCriteriaApiCancelConfirmOrderPaymentDTXAdapter cancelConfirmOrderPaymentDTXAdapter) {
        QueryBuilderRegistry.cancelConfirmOrderPaymentDTXAdapter = cancelConfirmOrderPaymentDTXAdapter;
    }

    @Autowired
    public void setCancelRecycleOrderDTXAdapter(SpringDataJpaCancelRecycleOrderDTXRepository.JpaCriteriaApiCancelRecycleOrderDTXAdapter cancelRecycleOrderDTXAdapter) {
        QueryBuilderRegistry.cancelRecycleOrderDTXAdapter = cancelRecycleOrderDTXAdapter;
    }

    @Autowired
    public void setCancelReserveOrderDTXAdapter(SpringDataJpaCancelReserveOrderDTXRepository.JpaCriteriaApiCancelReserveOrderDTXAdapter cancelReserveOrderDTXAdapter) {
        QueryBuilderRegistry.cancelReserveOrderDTXAdapter = cancelReserveOrderDTXAdapter;
    }


    @Autowired
    public void setConfirmOrderPaymentDTXAdapter(SpringDataJpaConfirmOrderPaymentDTXRepository.JpaCriteriaApiConfirmOrderPaymentDTXAdapter confirmOrderPaymentDTXAdapter) {
        QueryBuilderRegistry.confirmOrderPaymentDTXAdapter = confirmOrderPaymentDTXAdapter;
    }

    @Autowired
    public void setRecycleOrderDTXAdapter(SpringDataJpaRecycleOrderDTXRepository.JpaCriteriaApiRecycleOrderDTXAdapter recycleOrderDTXAdapter) {
        QueryBuilderRegistry.recycleOrderDTXAdapter = recycleOrderDTXAdapter;
    }

    @Autowired
    public void setReserveOrderDTXAdapter(SpringDataJpaReserveOrderDTXRepository.JpaCriteriaApiReserveOrderDTXAdapter reserveOrderDTXAdapter) {
        QueryBuilderRegistry.reserveOrderDTXAdapter = reserveOrderDTXAdapter;
    }

}
