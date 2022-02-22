package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_create_order_dtx.representation.CancelCreateOrderDTXCardRepresentation;
import com.mt.saga.appliction.cancel_create_order_dtx.representation.CancelCreateOrderDTXRepresentation;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.appliction.create_order_dtx.representation.CreateOrderDTXCardRepresentation;
import com.mt.saga.appliction.create_order_dtx.representation.CreateOrderDTXRepresentation;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "cancelCreateOrderDtx")
public class CancelCreateOrderDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<DistributedTx> dtx = ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, CancelCreateOrderDTXCardRepresentation::new));
    }
    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<DistributedTx> dtx = ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new CancelCreateOrderDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("admin/{id}/resolve")
    public ResponseEntity<?> resolveById(
            @PathVariable(name = "id") long id,
            @RequestBody ResolveReason resolveReason
    ) {
        ApplicationServiceRegistry.getCancelCreateOrderDTXApplicationService().resolve(id,resolveReason);
        return ResponseEntity.ok().build();
    }
}
