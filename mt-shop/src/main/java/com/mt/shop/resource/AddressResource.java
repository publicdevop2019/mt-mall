package com.mt.shop.resource;

import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.address.command.CustomerCreateAddressCommand;
import com.mt.shop.application.address.command.CustomerUpdateAddressCommand;
import com.mt.shop.application.address.representation.AddressCardRepresentation;
import com.mt.shop.application.address.representation.AddressRepresentation;
import com.mt.shop.application.address.representation.CustomerAddressCardRepresentation;
import com.mt.shop.application.address.representation.CustomerAddressRepresentation;
import com.mt.shop.domain.model.address.Address;
import com.mt.shop.domain.model.biz_order.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "addresses")
public class AddressResource {

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<Address> var0 = ApplicationServiceRegistry.getAddressApplicationService().addresses(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(var0, AddressCardRepresentation::new));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") String id) {
        Optional<Address> var0 = ApplicationServiceRegistry.getAddressApplicationService().address(id);
        return var0.map(value -> ResponseEntity.ok(new AddressRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @GetMapping("user")
    public ResponseEntity<?> readForUserByQuery(@RequestHeader("authorization") String authorization
    ) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        SumPagedRep<Address> var0 = ApplicationServiceRegistry.getAddressApplicationService().addresses();
        return ResponseEntity.ok(new SumPagedRep<>(var0, CustomerAddressCardRepresentation::new));
    }

    @GetMapping("user/{id}")
    public ResponseEntity<?> readForUserById(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        Optional<Address> var0 = ApplicationServiceRegistry.getAddressApplicationService().addressForUser(id);
        return var0.map(value -> ResponseEntity.ok(new CustomerAddressRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PostMapping("user")
    public ResponseEntity<?> createForUser(@RequestHeader("authorization") String authorization, @RequestBody CustomerCreateAddressCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        String domainId = ApplicationServiceRegistry.getAddressApplicationService().create(command, changeId);
        return ResponseEntity.ok().header("Location", domainId).build();
    }


    @PutMapping("user/{id}")
    public ResponseEntity<?> replaceForUserById(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestBody CustomerUpdateAddressCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        ApplicationServiceRegistry.getAddressApplicationService().replace(command, id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<?> deleteForAdminById(@RequestHeader("authorization") String authorization, @PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        UserThreadLocal.unset();
        UserThreadLocal.set(JwtUtility.getUserId(authorization));
        ApplicationServiceRegistry.getAddressApplicationService().remove(id, changeId);
        return ResponseEntity.ok().build();
    }
}
