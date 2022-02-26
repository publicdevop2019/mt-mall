package com.mt.shop.infrastructure;

public class AppConstant {
    public static final String APP_HANDLER = "_mall_handler";

    public static final String DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT = "decrease_order_storage_event";
    public static final String SAGA_REPLY_EVENT = "saga_reply_event";

    public static final String DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT = "decrease_actual_storage_for_conclude_event";

    public static final String INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT = "increase_order_storage_for_recycle_event";

    public static final String DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT = "decrease_order_storage_for_reserve_event";

    public static final String INCREASE_STORAGE_FOR_INVALID_EVENT = "increase_storage_for_invalid_event";
    public static final String GENERATE_PAYMENT_QR_LINK_EVENT = "generate_payment_qr_link_event";
    public static final String REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT = "remove_payment_qr_link_for_invalid_event";
    public static final String ORDER_OPERATION_EVENT = "order_operation_event";

    public static final String SAVE_NEW_ORDER_EVENT = "save_new_order_event";

    public static final String CLEAR_CART_EVENT = "clear_cart_event";

    public static final String UPDATE_ORDER_FOR_RESERVE_EVENT = "update_order_for_reserve_event";

    public static final String UPDATE_ORDER_PAYMENT_SUCCESS_EVENT = "update_order_payment_success_event";
    public static final String UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT = "update_order_for_update_order_address_event";
    public static final String REMOVE_ORDER_FOR_INVALID_EVENT = "remove_order_for_invalid_event";
    public static final String RESTORE_CART_FOR_INVALID_EVENT = "restore_cart_for_invalid_event";

    public static final String UPDATE_ORDER_FOR_CONCLUDE_EVENT = "update_order_for_conclude_event";

    public static final String UPDATE_ORDER_FOR_RECYCLE_EVENT = "update_order_for_recycle_event";
    public static final String LTX_FAILED_EVENT = "saga_ltx_failed";
}
