package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
@Getter
public class UserDetail {
    private Date modifiedByUserAt;
    private String userId;

    public UserDetail(String userId, Date modifiedByUserAt) {
        setUserId(userId);
        setModifiedByUserAt(modifiedByUserAt);
    }
    public UserDetail(String userId) {
        setUserId(userId);
    }

    public void setModifiedByUserAt(Date modifiedByUserAt) {
        Validator.notNull(modifiedByUserAt);
        this.modifiedByUserAt = modifiedByUserAt;
    }

    private void setUserId(String userId) {
        Validator.notNull(userId);
        this.userId = userId;
    }
}
