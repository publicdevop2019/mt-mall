package com.mt.shop.port.adapter.http;

import com.mt.common.domain.model.logging.ErrorMessage;
import com.mt.common.domain.model.restful.exception.AggregateOutdatedException;
import com.mt.shop.application.product.exception.AttributeNameNotFoundException;
import com.mt.shop.application.product.exception.NoLowestPriceFoundException;
import com.mt.shop.application.product.exception.SkuAlreadyExistException;
import com.mt.shop.application.product.exception.SkuNotExistException;
import com.mt.shop.domain.model.biz_order.exception.BizOrderCreationException;
import com.mt.shop.domain.model.biz_order.exception.BizOrderPaymentMismatchException;
import com.mt.shop.domain.model.biz_order.exception.BizOrderUpdateAddressAfterPaymentException;
import com.mt.shop.domain.model.biz_order.exception.VersionMismatchException;
import com.mt.shop.domain.model.image.FileSizeException;
import com.mt.shop.domain.model.image.FileTypeException;
import com.mt.shop.domain.model.image.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.mt.common.CommonConstant.HTTP_HEADER_ERROR_ID;


@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            SkuAlreadyExistException.class,
            SkuNotExistException.class,
            FileTypeException.class,
            FileSizeException.class,
            BizOrderPaymentMismatchException.class,
            BizOrderUpdateAddressAfterPaymentException.class,
            VersionMismatchException.class,
            AggregateOutdatedException.class
    })
    protected ResponseEntity<?> handle400Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {
            NoLowestPriceFoundException.class,
            AttributeNameNotFoundException.class,
            FileUploadException.class,
            BizOrderCreationException.class,
    })
    protected ResponseEntity<Object> handle500Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
