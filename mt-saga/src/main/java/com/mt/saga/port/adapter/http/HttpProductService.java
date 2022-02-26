package com.mt.saga.port.adapter.http;

import com.mt.common.domain.model.service_discovery.ServiceDiscovery;
import com.mt.saga.domain.model.order_state_machine.ProductService;
import com.mt.saga.domain.model.order_state_machine.order.CartDetail;
import com.mt.saga.domain.model.order_state_machine.product.ProductsSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;


@Service
@Slf4j
public class HttpProductService extends ProductService {

    @Value("${mt.url.mall.products}")
    private String productUrl;

    @Autowired
    private ServiceDiscovery eurekaHelper;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${mt.app.name.mt3}")
    private String appName;

    @Override
    public boolean validateOrderedProduct(List<CartDetail> customerOrderItemList) {
        log.info("start of validate order");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<CartDetail>> hashMapHttpEntity = new HttpEntity<>(customerOrderItemList, headers);

        List<String> collect = customerOrderItemList.stream().map(CartDetail::getProductId).collect(Collectors.toList());

        String query = "?" + HTTP_PARAM_QUERY + "=id:" + String.join(".", collect);
        String applicationUrl = eurekaHelper.getApplicationUrl(appName);

        ResponseEntity<ProductsSummary> exchange = restTemplate.exchange(applicationUrl + productUrl + query, HttpMethod.GET, hashMapHttpEntity, ProductsSummary.class);
        ProductsSummary body = exchange.getBody();
        boolean b = validateProducts(body, customerOrderItemList);
        log.info("end of validate order, result is {}", b);
        return b;
    }

}
