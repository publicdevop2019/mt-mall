package com.mt.shop.resource;

import com.mt.shop.application.ApplicationServiceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(produces = "application/json")
public class PaymentResource {

    @GetMapping("paymentStatus/{orderId}")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable("orderId") String orderId) {
        Boolean result = ApplicationServiceRegistry.getPaymentApplicationService().checkPaymentStatus(orderId);
        Map<String, Boolean> msg = new HashMap<>();
        msg.put("paymentStatus", result);
        return ResponseEntity.ok(msg);
    }
}
