package com.mt.saga.domain;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostDTXValidationService {


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

}
