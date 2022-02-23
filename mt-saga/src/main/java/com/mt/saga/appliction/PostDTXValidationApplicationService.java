package com.mt.saga.appliction;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import com.mt.saga.domain.model.distributed_tx.DistributedTxQuery;
import com.mt.saga.appliction.distributed_tx.command.DistributedTxSuccessEvent;
import com.mt.saga.infrastructure.AppConstant;
import org.springframework.stereotype.Service;

@Service
public class PostDTXValidationApplicationService {

    public void handle(DistributedTxSuccessEvent deserialize) {
        String[] changeIds = getChangeIds(deserialize.getChangeId());
        SumPagedRep<DistributedTx> query = DomainRegistry.getDistributedTxRepository().query(new DistributedTxQuery(changeIds[0]));
        if (query.getData().size() == 0 || query.getData().size() > 2) {
            throw new IllegalStateException("expected less then 2 but greater then 0");
        }
        if (query.getData().size() == 2) {
            DomainRegistry.getPostDtxValidationService().validate(query.getData().get(0), query.getData().get(1));
        }
    }

    private String[] getChangeIds(String changeId) {
        String var0;
        String var1;
        if (changeId.contains(AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX)) {
            var0 = changeId.replace(AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX, "");
            var1 = changeId;
        } else {
            var0 = changeId;
            var1 = changeId + AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        }
        return new String[]{var0, var1};
    }


}
