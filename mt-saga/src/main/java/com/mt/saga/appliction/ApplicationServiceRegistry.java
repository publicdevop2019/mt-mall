package com.mt.saga.appliction;

import com.mt.saga.appliction.cancel_conclude_order_dtx.CancelConcludeOrderDTXApplicationService;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTXApplicationService;
import com.mt.saga.appliction.cancel_create_order_dtx.CancelCreateOrderDTXApplicationService;
import com.mt.saga.appliction.cancel_invalid_order_dtx.CancelInvalidOrderDTXDTXApplicationService;
import com.mt.saga.appliction.cancel_recycle_order_dtx.CancelRecycleOrderDTXApplicationService;
import com.mt.saga.appliction.cancel_reserve_order_dtx.CancelReserveOrderDTXApplicationService;
import com.mt.saga.appliction.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTXApplicationService;
import com.mt.saga.appliction.conclude_order_dtx.ConcludeOrderDTXApplicationService;
import com.mt.saga.appliction.confirm_order_payment_dtx.ConfirmOrderPaymentDTXApplicationService;
import com.mt.saga.appliction.distributed_tx.DistributedTxApplicationService;
import com.mt.saga.appliction.invalid_order_dtx.InvalidOrderDTXDTXApplicationService;
import com.mt.saga.appliction.order_state_machine.OrderStateMachineApplicationService;
import com.mt.saga.appliction.recycle_order_dtx.RecycleOrderDTXApplicationService;
import com.mt.saga.appliction.reserve_order_dtx.ReserveOrderDTXApplicationService;
import com.mt.saga.appliction.update_order_address_dtx.UpdateOrderAddressDTXApplicationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Getter
    private static OrderStateMachineApplicationService stateMachineApplicationService;
    @Getter
    private static DistributedTxApplicationService distributedTxApplicationService;
    @Getter
    private static CancelCreateOrderDTXApplicationService cancelCreateOrderDTXApplicationService;
    @Getter
    private static ConfirmOrderPaymentDTXApplicationService confirmOrderPaymentDTXApplicationService;
    @Getter
    private static CancelConfirmOrderPaymentDTXApplicationService cancelConfirmOrderPaymentDTXApplicationService;
    @Getter
    private static CancelReserveOrderDTXApplicationService cancelReserveOrderDTXApplicationService;
    @Getter
    private static ReserveOrderDTXApplicationService reserveOrderDTXApplicationService;
    @Getter
    private static ConcludeOrderDTXApplicationService concludeOrderDTXApplicationService;
    @Getter
    private static CancelConcludeOrderDTXApplicationService cancelConcludeOrderDTXApplicationService;
    @Getter
    private static RecycleOrderDTXApplicationService recycleOrderDTXApplicationService;
    @Getter
    private static CancelRecycleOrderDTXApplicationService cancelRecycleOrderDTXApplicationService;

    @Autowired
    private void setCancelRecycleOrderDTXApplicationService(CancelRecycleOrderDTXApplicationService cancelRecycleOrderDTXApplicationService) {
        ApplicationServiceRegistry.cancelRecycleOrderDTXApplicationService = cancelRecycleOrderDTXApplicationService;
    }
    @Getter
    private static CancelUpdateOrderAddressDTXApplicationService cancelUpdateOrderAddressDTXApplicationService;

    @Autowired
    private void setCancelUpdateOrderAddressDTXApplicationService(CancelUpdateOrderAddressDTXApplicationService cancelUpdateOrderAddressDTXApplicationService) {
        ApplicationServiceRegistry.cancelUpdateOrderAddressDTXApplicationService = cancelUpdateOrderAddressDTXApplicationService;
    }
    @Getter
    private static UpdateOrderAddressDTXApplicationService updateOrderAddressDTXApplicationService;

    @Autowired
    private void setUpdateOrderAddressDTXApplicationService(UpdateOrderAddressDTXApplicationService updateOrderAddressDTXApplicationService) {
        ApplicationServiceRegistry.updateOrderAddressDTXApplicationService = updateOrderAddressDTXApplicationService;
    }
    @Getter
    private static InvalidOrderDTXDTXApplicationService invalidOrderDTXDTXApplicationService;

    @Autowired
    private void setInvalidOrderDTXDTXApplicationService(InvalidOrderDTXDTXApplicationService invalidOrderDTXDTXApplicationService) {
        ApplicationServiceRegistry.invalidOrderDTXDTXApplicationService = invalidOrderDTXDTXApplicationService;
    }
    @Getter
    private static CancelInvalidOrderDTXDTXApplicationService cancelInvalidOrderDTXDTXApplicationService;

    @Autowired
    private void setCancelInvalidOrderDTXDTXApplicationService(CancelInvalidOrderDTXDTXApplicationService cancelInvalidOrderDTXDTXApplicationService) {
        ApplicationServiceRegistry.cancelInvalidOrderDTXDTXApplicationService = cancelInvalidOrderDTXDTXApplicationService;
    }

    @Getter
    private static PostDTXValidationApplicationService postDtxValidationApplicationService;

    @Autowired
    private void setPostDtxValidationApplicationService(PostDTXValidationApplicationService postDtxValidationApplicationService) {
        ApplicationServiceRegistry.postDtxValidationApplicationService = postDtxValidationApplicationService;
    }

    @Autowired
    private void setCancelConcludeOrderDTXApplicationService(CancelConcludeOrderDTXApplicationService cancelConcludeOrderDTXApplicationService) {
        ApplicationServiceRegistry.cancelConcludeOrderDTXApplicationService = cancelConcludeOrderDTXApplicationService;
    }

    @Autowired
    private void setRecycleOrderDTXApplicationService(RecycleOrderDTXApplicationService recycleOrderDTXApplicationService) {
        ApplicationServiceRegistry.recycleOrderDTXApplicationService = recycleOrderDTXApplicationService;
    }

    @Autowired
    private void setConcludeOrderDTXApplicationService(ConcludeOrderDTXApplicationService concludeOrderDTXApplicationService) {
        ApplicationServiceRegistry.concludeOrderDTXApplicationService = concludeOrderDTXApplicationService;
    }

    @Autowired
    private void setReserveOrderDTXApplicationService(ReserveOrderDTXApplicationService reserveOrderDTXApplicationService) {
        ApplicationServiceRegistry.reserveOrderDTXApplicationService = reserveOrderDTXApplicationService;
    }

    @Autowired
    private void setCancelReserveOrderDTXApplicationService(CancelReserveOrderDTXApplicationService cancelReserveOrderDTXApplicationService) {
        ApplicationServiceRegistry.cancelReserveOrderDTXApplicationService = cancelReserveOrderDTXApplicationService;
    }

    @Autowired
    private void setCancelConfirmOrderPaymentDTXApplicationService(CancelConfirmOrderPaymentDTXApplicationService cancelConfirmOrderPaymentDTXApplicationService) {
        ApplicationServiceRegistry.cancelConfirmOrderPaymentDTXApplicationService = cancelConfirmOrderPaymentDTXApplicationService;
    }

    @Autowired
    private void setStateMachineApplicationService(OrderStateMachineApplicationService stateMachineApplicationService) {
        ApplicationServiceRegistry.stateMachineApplicationService = stateMachineApplicationService;
    }

    @Autowired
    private void setConfirmOrderPaymentDTXApplicationService(ConfirmOrderPaymentDTXApplicationService confirmOrderPaymentDTXApplicationService) {
        ApplicationServiceRegistry.confirmOrderPaymentDTXApplicationService = confirmOrderPaymentDTXApplicationService;
    }

    @Autowired
    private void setCancelCreateOrderDTXApplicationService(CancelCreateOrderDTXApplicationService cancelCreateOrderDTXApplicationService) {
        ApplicationServiceRegistry.cancelCreateOrderDTXApplicationService = cancelCreateOrderDTXApplicationService;
    }

    @Autowired
    private void setDistributedTxApplicationService(DistributedTxApplicationService distributedTxApplicationService) {
        ApplicationServiceRegistry.distributedTxApplicationService = distributedTxApplicationService;
    }

}
