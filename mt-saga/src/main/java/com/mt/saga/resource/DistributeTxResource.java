package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.distributed_tx.command.ResolveReason;
import com.mt.saga.appliction.distributed_tx.representation.DistributedTxCardRepresentation;
import com.mt.saga.appliction.distributed_tx.representation.DistributedTxRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "dtx")
public class DistributeTxResource {
    @GetMapping
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<DistributedTx> dtx = ApplicationServiceRegistry.getDistributedTxApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, DistributedTxCardRepresentation::new));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<DistributedTx> dtx = ApplicationServiceRegistry.getDistributedTxApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new DistributedTxRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("{dtxId}/cancel")
    public ResponseEntity<?> cancelCreateOrderDtx(
            @PathVariable("dtxId") long dtxId
    ) {
        ApplicationServiceRegistry.getDistributedTxApplicationService().cancel(dtxId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("admin/{dtxId}/resolved")
    public ResponseEntity<?> resolveDtx(
            @PathVariable("dtxId") long dtxId,
            @RequestBody ResolveReason reason
    ) {
        ApplicationServiceRegistry.getDistributedTxApplicationService().resolve(dtxId, reason);
        return ResponseEntity.ok().build();
    }

}
