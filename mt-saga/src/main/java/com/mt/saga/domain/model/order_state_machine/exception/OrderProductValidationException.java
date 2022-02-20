package com.mt.saga.domain.model.order_state_machine.exception;

public class OrderProductValidationException extends RuntimeException{
    public OrderProductValidationException(Throwable cause) {
        super("error during validate order", cause);
    }
}
