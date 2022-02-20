package com.mt.user_profile.domain.biz_order;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum BizOrderStatus {
    NOT_PAID_RESERVED,
    NOT_PAID_RECYCLED,
    PAID_RESERVED,
    PAID_RECYCLED,
    CONFIRMED,
    CANCELLED,
    DRAFT,
    ;
}
