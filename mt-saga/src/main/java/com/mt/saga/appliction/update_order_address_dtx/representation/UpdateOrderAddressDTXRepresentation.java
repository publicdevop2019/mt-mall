package com.mt.saga.appliction.update_order_address_dtx.representation;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.clazz.ClassUtility;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.saga.appliction.common.CommonDTXRepresentation;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderForUpdateOrderAddressEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateOrderAddressDTXRepresentation extends CommonDTXRepresentation {

    private static final String UPDATE_ORDER_LTX = "UPDATE_ORDER_LTX";

    public UpdateOrderAddressDTXRepresentation(UpdateOrderAddressDTX var0) {
        setId(var0.getId());
        setStatus(var0.getStatus());
        setChangeId(var0.getChangeId());
        setOrderId(var0.getOrderId());
        setCreatedAt(var0.getCreatedAt().getTime());
        statusMap.put(UPDATE_ORDER_LTX, var0.getUpdateOrderLTXStatus());
        emptyOptMap.put(UPDATE_ORDER_LTX, var0.isUpdateOrderLTXEmptyOpt());

        SumPagedRep<StoredEvent> query = CommonDomainRegistry.getEventRepository().query(
                new StoredEventQuery("domainId:" + var0.getId(),
                        PageConfig.defaultConfig().getRawValue()
                        , QueryConfig.skipCount().value()));
        query.getData().forEach(e->{
            if(UpdateOrderForUpdateOrderAddressEvent.name.equalsIgnoreCase(ClassUtility.getShortName(e.getName()))){
                idMap.put(UPDATE_ORDER_LTX,e.getId());
            }
        });
    }
}

