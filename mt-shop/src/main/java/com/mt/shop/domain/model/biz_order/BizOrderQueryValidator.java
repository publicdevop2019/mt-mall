package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class BizOrderQueryValidator {
    private BizOrderQuery bizOrderQuery;
    private ValidationNotificationHandler handler;

    public BizOrderQueryValidator(BizOrderQuery bizOrderQuery, ValidationNotificationHandler handler) {
        this.bizOrderQuery = bizOrderQuery;
        this.handler = handler;
    }
    public void validate(){
        if(!bizOrderQuery.isAdmin()){
            if(bizOrderQuery.getUserId()==null||bizOrderQuery.getUserId().isBlank())
                handler.handleError("user id must present for non admin query");
        }
    }
}
