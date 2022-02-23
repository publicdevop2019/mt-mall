package com.mt.saga.appliction.cancel_update_order_address_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderForUpdateOrderAddressEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelUpdateOrderAddressDTXRepresentation extends CommonDTXRepresentation {

    private static final String CANCEL_UPDATE_ORDER_LTX = "CANCEL_UPDATE_ORDER_LTX";

    public CancelUpdateOrderAddressDTXRepresentation(CancelUpdateOrderAddressDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(CANCEL_UPDATE_ORDER_LTX, var0.getCancelUpdateOrderLTXStatus());
        emptyOptMap.put(CANCEL_UPDATE_ORDER_LTX, var0.isCancelUpdateOrderLTXEmptyOpt());

        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(CancelUpdateOrderForUpdateOrderAddressEvent.name.equalsIgnoreCase(e.getName())){
                idMap.put(CANCEL_UPDATE_ORDER_LTX,e.getId());
            }
        });
    }
}
