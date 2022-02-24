import { IForm } from 'mt-form-builder/lib/classes/template.interface';
export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "select",
            "display": true,
            "label": "PLEASE_SELECT_TASK_NAME",
            "key": "taskName",
            "position": {
                "row": "0",
                "column": "0"
            },
            "options": [
                { label: 'CREATE_ORDER_DTX', value: "CreateOrderDtx" },
                { label: 'CANCEL_CREATE_ORDER_DTX', value: "CreateOrderDtx_cancel" },
                { label: 'RESERVE_ORDER_DTX', value: "ReserveOrderDtx" },
                { label: 'CANCEL_RESERVE_ORDER_DTX', value: "ReserveOrderDtx_cancel" },
                { label: 'RECYCLE_ORDER_DTX', value: "RecycleOrderDtx" },
                { label: 'CANCEL_RECYCLE_ORDER_DTX', value: "RecycleOrderDtx_cancel" },
                { label: 'CONFIRM_PAYMENT_DTX', value: "ConfirmOrderPaymentOrderDtx" },
                { label: 'CANCEL_CONFIRM_PAYMENT_DTX', value: "ConfirmOrderPaymentOrderDtx_cancel" },
                { label: 'CONCLUDE_ORDER_DTX', value: "ConcludeOrderDtx" },
                { label: 'CANCEL_CONCLUDE_ORDER_DTX', value: "ConcludeOrderDtx_cancel" },
                { label: 'UPDATE_ORDER_ADDRESS_DTX', value: "UpdateOrderAddressDtx" },
                { label: 'CANCEL_UPDATE_ORDER_ADDRESS_DTX', value: "UpdateOrderAddressDtx_cancel" },
                { label: 'INVALID_ORDER_DTX', value: "InvalidOrderDtx" },
                { label: 'CANCEL_INVALID_ORDER_DTX', value: "InvalidOrderDtx_cancel" },
            ],
        },
    ],
}
