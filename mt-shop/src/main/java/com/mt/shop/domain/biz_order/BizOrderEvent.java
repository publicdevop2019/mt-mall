package com.mt.shop.domain.biz_order;


/**
 * use separate prepare event so logic will not miss triggered
 */
public enum BizOrderEvent {
    CONFIRM_PAYMENT,
    UPDATE_ADDRESS,
    CANCEL_ORDER,
    CONFIRM_ORDER,
    NEW_ORDER,
    RECYCLE_ORDER_STORAGE,
    RESERVE;
}
