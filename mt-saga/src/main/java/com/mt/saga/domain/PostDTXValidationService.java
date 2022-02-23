package com.mt.saga.domain;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostDTXValidationService {

    public void validate(ReserveOrderDTX reserveOrderDTX, CancelReserveOrderDTX cancelReserveOrderDTX) {
        if (reserveOrderDTX.getStatus().equals(DTXStatus.SUCCESS) && cancelReserveOrderDTX.getStatus().equals(DTXStatus.SUCCESS)) {
            if (
                    (reserveOrderDTX.isDecreaseOrderStorageLTXEmptyOpt() && !cancelReserveOrderDTX.isCancelDecreaseOrderStorageLTXEmptyOpt())
                            ||
                            (reserveOrderDTX.isUpdateOrderLTXEmptyOpt() && !cancelReserveOrderDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!reserveOrderDTX.isUpdateOrderLTXEmptyOpt() && cancelReserveOrderDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!reserveOrderDTX.isDecreaseOrderStorageLTXEmptyOpt() && cancelReserveOrderDTX.isCancelDecreaseOrderStorageLTXEmptyOpt())
            ) {
                notifyAdmin(reserveOrderDTX.getId(), cancelReserveOrderDTX.getId(), reserveOrderDTX.getOrderId());
            } else {
                log.debug("post reserve/cancel create dtx validation success");
            }
        }
    }

    public void validate(RecycleOrderDTX recycleOrderDTX, CancelRecycleOrderDTX cancelRecycleOrderDTX) {
        if (recycleOrderDTX.getStatus().equals(DTXStatus.SUCCESS) && cancelRecycleOrderDTX.getStatus().equals(DTXStatus.SUCCESS)) {
            if (
                    (recycleOrderDTX.isUpdateOrderLTXEmptyOpt() && !cancelRecycleOrderDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!recycleOrderDTX.isUpdateOrderLTXEmptyOpt() && cancelRecycleOrderDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!recycleOrderDTX.isIncreaseOrderStorageLTXEmptyOpt() && cancelRecycleOrderDTX.isCancelIncreaseOrderStorageLTXEmptyOpt())
                            ||
                            (recycleOrderDTX.isIncreaseOrderStorageLTXEmptyOpt() && !cancelRecycleOrderDTX.isCancelIncreaseOrderStorageLTXEmptyOpt())
            ) {
                notifyAdmin(recycleOrderDTX.getId(), cancelRecycleOrderDTX.getId(), recycleOrderDTX.getOrderId());
            } else {
                log.debug("post recycle/cancel create dtx validation success");
            }
        }
    }

    public void validate(DistributedTx createOrderDTX, DistributedTx cancelCreateOrderDTX) {
        if (createOrderDTX.getStatus().equals(DTXStatus.SUCCESS) && cancelCreateOrderDTX.getStatus().equals(DTXStatus.SUCCESS)) {
//            if (
//                    (createOrderDTX.isLocalTxEmptyOptByName(APP_DECREASE_ORDER_STORAGE) && !cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_DECREASE_ORDER_STORAGE))
//                            ||
//                            (!createOrderDTX.isLocalTxEmptyOptByName(APP_DECREASE_ORDER_STORAGE) && cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_DECREASE_ORDER_STORAGE))
//                            ||
//                            (createOrderDTX.isLocalTxEmptyOptByName(APP_CLEAR_CART) && !cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_CLEAR_CART))
//                            ||
//                            (!createOrderDTX.isLocalTxEmptyOptByName(APP_CLEAR_CART) && cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_CLEAR_CART))
//                            ||
//                            (!createOrderDTX.isLocalTxEmptyOptByName(APP_SAVE_NEW_ORDER) && cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_SAVE_NEW_ORDER))
//                            ||
//                            (createOrderDTX.isLocalTxEmptyOptByName(APP_SAVE_NEW_ORDER) && !cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_SAVE_NEW_ORDER))
//                            ||
//                            (!createOrderDTX.isLocalTxEmptyOptByName(APP_GENERATE_PAYMENT_QR_LINK) && cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_GENERATE_PAYMENT_QR_LINK))
//                            ||
//                            (createOrderDTX.isLocalTxEmptyOptByName(APP_GENERATE_PAYMENT_QR_LINK) && !cancelCreateOrderDTX.isLocalTxEmptyOptByName(APP_CANCEL_GENERATE_PAYMENT_QR_LINK))
//            ) {
//                notifyAdmin(createOrderDTX.getId(),cancelCreateOrderDTX.getId(), createOrderDTX.getLockId());
//            }else{
//                log.debug("post create/cancel create dtx validation success");
//            }
        }
    }

    public void validate(ConcludeOrderDTX concludeOrderDTX, CancelConcludeOrderDTX cancelConcludeOrderDTX) {
        if (concludeOrderDTX.getStatus().equals(DTXStatus.SUCCESS) && cancelConcludeOrderDTX.getStatus().equals(DTXStatus.SUCCESS)) {
            if (
                    (concludeOrderDTX.isUpdateOrderLTXEmptyOpt() && !cancelConcludeOrderDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!concludeOrderDTX.isUpdateOrderLTXEmptyOpt() && cancelConcludeOrderDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!concludeOrderDTX.isDecreaseActualStorageLTXEmptyOpt() && cancelConcludeOrderDTX.isCancelDecreaseActualStorageLTXEmptyOpt())
                            ||
                            (concludeOrderDTX.isDecreaseActualStorageLTXEmptyOpt() && !cancelConcludeOrderDTX.isCancelDecreaseActualStorageLTXEmptyOpt())
            ) {
                notifyAdmin(concludeOrderDTX.getId(), cancelConcludeOrderDTX.getId(), concludeOrderDTX.getOrderId());
            } else {
                log.debug("post conclude/cancel create dtx validation success");
            }

        }
    }

    public void validate(ConfirmOrderPaymentDTX confirmOrderPaymentDTX, CancelConfirmOrderPaymentDTX cancelConfirmOrderPaymentDTX) {
        if (confirmOrderPaymentDTX.getStatus().equals(DTXStatus.SUCCESS) && cancelConfirmOrderPaymentDTX.getStatus().equals(DTXStatus.SUCCESS)) {
            if (
                    (confirmOrderPaymentDTX.isUpdateOrderLTXEmptyOpt() && !cancelConfirmOrderPaymentDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!confirmOrderPaymentDTX.isUpdateOrderLTXEmptyOpt() && cancelConfirmOrderPaymentDTX.isCancelUpdateOrderLTXEmptyOpt())
            ) {
                notifyAdmin(confirmOrderPaymentDTX.getId(), cancelConfirmOrderPaymentDTX.getId(), confirmOrderPaymentDTX.getOrderId());
            } else {
                log.debug("post confirmPayment/cancel create dtx validation success");
            }
        }
    }


    private void notifyAdmin(Long id, Long id1, String orderId) {
        log.debug("validation failed, this will be reported to admin");
        MallNotificationEvent mallNotificationEvent = MallNotificationEvent.create("POST_DTX_VALIDATION_FAILED");
        mallNotificationEvent.addDetail("DTX", String.valueOf(id));
        mallNotificationEvent.addDetail("CANCEL_DTX", String.valueOf(id1));
        mallNotificationEvent.setOrderId(orderId);
        StoredEvent storedEvent = new StoredEvent(mallNotificationEvent);
        storedEvent.setIdExplicitly(0);
        CommonDomainRegistry.getEventStreamService().next(storedEvent);
    }

    public void validate(UpdateOrderAddressDTX updateOrderAddressDTX, CancelUpdateOrderAddressDTX cancelUpdateOrderAddressDTX) {
        if (updateOrderAddressDTX.getStatus().equals(DTXStatus.SUCCESS) && cancelUpdateOrderAddressDTX.getStatus().equals(DTXStatus.SUCCESS)) {
            if (
                    (updateOrderAddressDTX.isUpdateOrderLTXEmptyOpt() && !cancelUpdateOrderAddressDTX.isCancelUpdateOrderLTXEmptyOpt())
                            ||
                            (!updateOrderAddressDTX.isUpdateOrderLTXEmptyOpt() && cancelUpdateOrderAddressDTX.isCancelUpdateOrderLTXEmptyOpt())
            ) {
                notifyAdmin(updateOrderAddressDTX.getId(), cancelUpdateOrderAddressDTX.getId(), updateOrderAddressDTX.getOrderId());
            } else {
                log.debug("post update/cancel address dtx validation success");
            }
        }
    }

}
