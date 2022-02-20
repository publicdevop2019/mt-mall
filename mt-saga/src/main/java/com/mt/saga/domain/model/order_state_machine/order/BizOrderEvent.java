package com.mt.saga.domain.model.order_state_machine.order;

import com.mt.common.domain.model.sql.converter.EnumConverter;

/**
 * use separate prepare event so logic will not miss triggered
 */
public enum BizOrderEvent {
    CONFIRM_PAYMENT,
    UPDATE_ADDRESS,
    CANCEL_ORDER,
    CONFIRM_PAYMENT_SUCCESS,
    CONFIRM_ORDER,
    NEW_ORDER,
    RECYCLE_ORDER_STORAGE,
    RESERVE;

    public static class DBConverter extends EnumConverter<BizOrderEvent> {
        public DBConverter() {
            super(BizOrderEvent.class);
        }
    }

}
