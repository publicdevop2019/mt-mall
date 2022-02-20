package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.invalid_order_dtx.representation.InvalidOrderDTXCardRepresentation;
import com.mt.saga.appliction.invalid_order_dtx.representation.InvalidOrderDTXRepresentation;
import com.mt.saga.appliction.update_order_address_dtx.representation.UpdateOrderAddressDTXCardRepresentation;
import com.mt.saga.appliction.update_order_address_dtx.representation.UpdateOrderAddressDTXRepresentation;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "invalidOrderDtx")
public class InvalidOrderDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<InvalidOrderDTX> dtx = ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, InvalidOrderDTXCardRepresentation::new));
    }

    @PostMapping("admin/{dtxId}/cancel")
    public ResponseEntity<?> cancelCreateOrderDtx(
            @PathVariable("dtxId") long dtxId
    ) {
        ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().cancel(dtxId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<InvalidOrderDTX> dtx = ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new InvalidOrderDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
