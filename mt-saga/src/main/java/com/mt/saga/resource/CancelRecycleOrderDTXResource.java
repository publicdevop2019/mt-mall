package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_recycle_order_dtx.representation.CancelRecycleOrderDTXCardRepresentation;
import com.mt.saga.appliction.cancel_recycle_order_dtx.representation.CancelRecycleOrderDTXRepresentation;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "cancelRecycleOrderDtx")
public class CancelRecycleOrderDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<DistributedTx> dtx = ApplicationServiceRegistry.getCancelRecycleOrderDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, CancelRecycleOrderDTXCardRepresentation::new));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<DistributedTx> dtx = ApplicationServiceRegistry.getCancelRecycleOrderDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new CancelRecycleOrderDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("admin/{id}/resolve")
    public ResponseEntity<?> resolveById(
            @PathVariable(name = "id") long id,
            @RequestBody ResolveReason resolveReason
    ) {
        ApplicationServiceRegistry.getCancelRecycleOrderDTXApplicationService().resolve(id, resolveReason);
        return ResponseEntity.ok().build();
    }
}
