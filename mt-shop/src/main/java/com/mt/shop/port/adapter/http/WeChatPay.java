package com.mt.shop.port.adapter.http;

import com.mt.shop.application.ApplicationServiceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

@RestController
@RequestMapping(produces = "application/json")
public class WeChatPay {

    @PostMapping("afterQRScanCallback")
    public ResponseEntity<?> afterQRScanCallback(@RequestParam("openid") String openId, @RequestParam("productid") String productId, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getPaymentApplicationService().handleQRCodeScan(openId, productId, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("paySuccessCallback")
    public ResponseEntity<?> paySuccessCallback(@RequestParam("productid") String productId) {
        return ResponseEntity.ok().build();
    }
}
