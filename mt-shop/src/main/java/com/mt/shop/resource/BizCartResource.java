package com.mt.shop.resource;

import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.cart.command.UserCreateBizCartItemCommand;
import com.mt.shop.application.cart.representation.UserBizCartItemCardRep;
import com.mt.shop.domain.model.biz_order.UserThreadLocal;
import com.mt.shop.domain.model.cart.BizCart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;


@RestController
@RequestMapping(produces = "application/json", path = "cart")
public class BizCartResource {

    @GetMapping("user")
    public ResponseEntity<?> readForUserByQuery(@RequestHeader("authorization") String authorization) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        SumPagedRep<BizCart> var0 = ApplicationServiceRegistry.getCartApplicationService().getAllInCart();
        return ResponseEntity.ok(new SumPagedRep<>(var0, UserBizCartItemCardRep::new));
    }

    @PostMapping("user")
    public ResponseEntity<Void> createForUser(@RequestHeader("authorization") String authorization, @RequestBody UserCreateBizCartItemCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        String s = ApplicationServiceRegistry.getCartApplicationService().addToCart(command, changeId);
        return ResponseEntity.ok().header("Location", s).build();
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<Void> deleteForUserById(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        ApplicationServiceRegistry.getCartApplicationService().removeFromCart(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("app")
    public ResponseEntity<Void> restoreDeleted(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        ApplicationServiceRegistry.getCartApplicationService().restoreRemoved(queryParam, changeId);
        return ResponseEntity.ok().build();
    }
}
