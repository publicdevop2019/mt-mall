package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.conclude_order_dtx.representation.ConcludeOrderDTXCardRepresentation;
import com.mt.saga.appliction.conclude_order_dtx.representation.ConcludeOrderDTXRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "concludeOrderDtx")
public class ConcludeOrderDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<DistributedTx> dtx = ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, ConcludeOrderDTXCardRepresentation::new));
    }

    @PostMapping("admin/{dtxId}/cancel")
    public ResponseEntity<?> cancelCreateOrderDtx(
            @PathVariable("dtxId") long dtxId
    ) {
        ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().cancel(dtxId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<DistributedTx> dtx = ApplicationServiceRegistry.getConcludeOrderDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new ConcludeOrderDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
