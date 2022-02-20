package com.mt.saga.domain.model.order_state_machine.exception;

public class OrderPaymentValidationException extends RuntimeException{
    public OrderPaymentValidationException(Throwable cause) {
        super("error during validate order", cause);
    }
}
