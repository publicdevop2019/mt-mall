package com.mt.saga.port.adapter.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.common.domain.model.service_discovery.ServiceDiscovery;
import com.mt.saga.domain.model.order_state_machine.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
@Slf4j
public class HttpPaymentService implements PaymentService {

    @Value("${mt.url.payment.wechat.confirm}")
    private String confirmUrl;

    @Value("${mt.app.name.mt6}")
    private String appName;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private ServiceDiscovery eurekaHelper;

    @Override
    public Boolean confirmPaymentStatus(String orderId) {
        log.info("starting confirmPaymentStatus");
        ParameterizedTypeReference<HashMap<String, Boolean>> responseType =
                new ParameterizedTypeReference<>() {
                };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(headers);
        String applicationUrl = eurekaHelper.getApplicationUrl(appName);
        ResponseEntity<HashMap<String, Boolean>> exchange = restTemplate.exchange(applicationUrl + confirmUrl + "/" + orderId, HttpMethod.GET, hashMapHttpEntity, responseType);
        Boolean result = null;
        if (exchange.getBody() != null) {
            result = exchange.getBody().get("paymentStatus");
        } else {
            log.error("unable to extract paymentStatus from response");
        }
        log.info("complete confirmPaymentStatus");
        return result;

    }
}

