package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_conclude_order_dtx.representation.CancelConcludeOrderDTXCardRepresentation;
import com.mt.saga.appliction.cancel_conclude_order_dtx.representation.CancelConcludeOrderDTXRepresentation;
import com.mt.saga.appliction.cancel_invalid_order_dtx.representation.CancelInvalidOrderDTXCardRepresentation;
import com.mt.saga.appliction.cancel_invalid_order_dtx.representation.CancelInvalidOrderDTXRepresentation;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "cancelInvalidOrderDtx")
public class CancelInvalidOrderDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<CancelInvalidOrderDTX> dtx = ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, CancelInvalidOrderDTXCardRepresentation::new));
    }
    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<CancelInvalidOrderDTX> dtx = ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new CancelInvalidOrderDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }
    @PostMapping("admin/{id}/resolve")
    public ResponseEntity<?> resolveById(
            @PathVariable(name = "id") long id,
            @RequestBody ResolveReason resolveReason
    ) {
        ApplicationServiceRegistry.getCancelInvalidOrderDTXDTXApplicationService().resolve(id,resolveReason);
        return ResponseEntity.ok().build();
    }
}
