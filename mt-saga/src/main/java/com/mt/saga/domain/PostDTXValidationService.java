package com.mt.saga.domain;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.infrastructure.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostDTXValidationService {


    public void validate(DistributedTx forwardDtx, DistributedTx backwardDtx) {
        if (forwardDtx.getStatus().equals(DTXStatus.SUCCESS) && backwardDtx.getStatus().equals(DTXStatus.SUCCESS)) {
            boolean b = forwardDtx.getLocalTxs().values().stream().anyMatch(e -> (
                    !e.isEmptyOperation() && forwardDtx.isLocalTxEmptyOptByName(Utility.getCancelLtxName(e.getName()))
                            ||
                            e.isEmptyOperation() && !forwardDtx.isLocalTxEmptyOptByName(Utility.getCancelLtxName(e.getName()))
            ));
            if (b) {
                notifyAdmin(forwardDtx.getId(), backwardDtx.getId(), forwardDtx.getLockId());
            } else {
                log.debug("post dtx validation success");
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

}
