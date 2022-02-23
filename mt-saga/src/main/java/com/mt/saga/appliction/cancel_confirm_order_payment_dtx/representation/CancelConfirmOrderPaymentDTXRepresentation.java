package com.mt.saga.appliction.cancel_confirm_order_payment_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelUpdateOrderPaymentEvent;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelConfirmOrderPaymentDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_UPDATE_ORDER_LTX = "CANCEL_UPDATE_ORDER_LTX";

    public CancelConfirmOrderPaymentDTXRepresentation(DistributedTx var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getLockId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_UPDATE_ORDER_LTX, var0.getLocalTxStatusByName(CancelUpdateOrderPaymentEvent.name));
        emptyOptMap.put(CANCEL_UPDATE_ORDER_LTX, var0.isLocalTxEmptyOptByName(CancelUpdateOrderPaymentEvent.name));

        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e -> {
            if (CancelUpdateOrderPaymentEvent.name.equalsIgnoreCase(e.getName())) {
                idMap.put(CANCEL_UPDATE_ORDER_LTX, e.getId());
            }
        });
    }
}
