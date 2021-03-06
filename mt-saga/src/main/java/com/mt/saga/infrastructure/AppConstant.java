package com.mt.saga.infrastructure;

public class AppConstant {
    public static final String APP_HANDLER_SUFFIX = "_saga_handler";
    public static final String APP_CHANGE_ID_CANCEL_SUFFIX = "_cancel";
    //start of create
    public static final String SAVE_NEW_ORDER_FOR_CREATE_EVENT = "save_new_order_event";
    public static final String DECREASE_ORDER_STORAGE_FOR_CREATE_EVENT = "decrease_order_storage_event";
    public static final String CLEAR_CART_FOR_CREATE_EVENT = "clear_cart_event";
    public static final String GENERATE_PAYMENT_QR_LINK_FOR_CREATE_EVENT = "generate_payment_qr_link_event";
    //end of create

    //start of invalid
    public static final String INCREASE_STORAGE_FOR_INVALID_EVENT = "increase_storage_for_invalid_event";
    public static final String REMOVE_ORDER_FOR_INVALID_EVENT = "remove_order_for_invalid_event";
    public static final String REMOVE_PAYMENT_QR_LINK_FOR_INVALID_EVENT = "remove_payment_qr_link_for_invalid_event";
    //end of invalid

    //start of reserve
    public static final String UPDATE_ORDER_FOR_RESERVE_EVENT = "update_order_for_reserve_event";
    public static final String DECREASE_ORDER_STORAGE_FOR_RESERVE_EVENT = "decrease_order_storage_for_reserve_event";
    //end of reserve

    //start of conclude
    public static final String UPDATE_ORDER_FOR_CONCLUDE_EVENT = "update_order_for_conclude_event";
    public static final String DECREASE_ACTUAL_STORAGE_FOR_CONCLUDE_EVENT = "decrease_actual_storage_for_conclude_event";
    //end of conclude

    //start of recycle
    public static final String UPDATE_ORDER_FOR_RECYCLE_EVENT = "update_order_for_recycle_event";
    public static final String INCREASE_ORDER_STORAGE_FOR_RECYCLE_EVENT = "increase_order_storage_for_recycle_event";
    //end of recycle

    //start of confirm order payment
    public static final String UPDATE_ORDER_FOR_PAYMENT_SUCCESS_EVENT = "update_order_payment_success_event";
    //end of confirm order payment

    //start of update order address
    public static final String UPDATE_ORDER_FOR_UPDATE_ORDER_ADDRESS_EVENT = "update_order_for_update_order_address_event";
    //end of update order address
    public static final String DTX_SUCCESS_EVENT = "dtx_success_event";

    //start of create dtx
    public static final String CREATE_CREATE_ORDER_DTX_EVENT = "create_create_order_dtx_event";
    public static final String CREATE_INVALID_ORDER_DTX_EVENT = "create_invalid_order_dtx_event";

    public static final String CREATE_RECYCLE_ORDER_DTX_EVENT = "create_recycle_order_dtx_event";

    public static final String CREATE_UPDATE_ORDER_ADDRESS_DTX_EVENT = "create_update_order_address_dtx_event";

    public static final String CREATE_CONFIRM_ORDER_PAYMENT_DTX_EVENT = "create_confirm_order_payment_dtx_event";

    public static final String CREATE_RESERVE_ORDER_DTX_EVENT = "create_reserve_order_dtx_event";

    public static final String CREATE_CONCLUDE_ORDER_DTX_EVENT = "create_conclude_order_dtx_event";
    //end of create dtx

    public static final String ORDER_OPERATION_EVENT = "order_operation_event";
    public static final String RESTORE_CART_FOR_INVALID_EVENT = "restore_cart_for_invalid_event";


    public static final String CANCEL_DTX_EVENT = "sage_cancel_dtx_event";
    public static final String SAGA_REPLY_EVENT = "saga_reply_event";
    public static final String LTX_FAILED_EVENT = "saga_ltx_failed";
}
