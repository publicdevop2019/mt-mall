package com.mt.user_profile.resource;

import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.user_profile.application.ApplicationServiceRegistry;
import com.mt.user_profile.application.biz_order.command.*;
import com.mt.user_profile.application.biz_order.representation.*;
import com.mt.user_profile.domain.biz_order.BizOrderSummary;
import com.mt.user_profile.domain.biz_order.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.mt.common.CommonConstant.*;


@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "orders")
public class BizOrderResource {

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        UserThreadLocal.unset();
        SumPagedRep<BizOrderSummary> var0 = ApplicationServiceRegistry.getBizOrderApplicationService().orders(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(var0, AdminBizOrderCardRepresentation::new));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") String id) {
        UserThreadLocal.unset();
        Optional<BizOrderSummary> var0 = ApplicationServiceRegistry.getBizOrderApplicationService().orderOfId(id);
        return var0.map(value -> ResponseEntity.ok(new AdminBizOrderRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @GetMapping("user")
    public ResponseEntity<?> readForUserByQuery(
            @RequestHeader("authorization") String authorization,
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        SumPagedRep<BizOrderSummary> var0 = ApplicationServiceRegistry.getBizOrderApplicationService().ordersForUser(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(var0, CustomerBizOrderCardRepresentation::new));
    }

    @GetMapping("user/{id}")
    public ResponseEntity<?> readForUserById(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        Optional<BizOrderSummary> var0 = ApplicationServiceRegistry.getBizOrderApplicationService().orderOfIdForUser(id);
        return var0.map(value -> ResponseEntity.ok(new CustomerBizOrderRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PostMapping("user")
    public ResponseEntity<Void> createForUser(
            @RequestHeader("authorization") String authorization,
            @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
            @RequestBody CustomerPlaceBizOrderCommand command) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getBizOrderApplicationService().handleUserPlaceOrderAction(command, changeId)).build();
    }

    @PutMapping("user/{id}")
    public ResponseEntity<Void>  updateAddress(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
                                                                   @RequestBody CustomerUpdateBizOrderAddressCommand command) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        ApplicationServiceRegistry.getBizOrderApplicationService().updateAddress(command, id, changeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("user/{id}/confirm")
    public ResponseEntity<?> confirmOrder(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        ApplicationServiceRegistry.getBizOrderApplicationService().confirmPayment(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("user/{id}/reserve")
    public ResponseEntity<Void> reserveForUser(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        ApplicationServiceRegistry.getBizOrderApplicationService().reserveOrder(id, changeId);
        return ResponseEntity.ok().header("Location", id).build();
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<Void> deleteForUserById(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
         ApplicationServiceRegistry.getBizOrderApplicationService().deleteOrderByUser(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public CompletableFuture<ResponseEntity<Object>> deleteForAdminById(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,@RequestHeader("version") Integer version) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        return ApplicationServiceRegistry.getBizOrderApplicationService().deleteOrder(id, changeId,version);
    }

}
