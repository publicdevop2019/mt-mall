package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.recycle_order_dtx.representation.RecycleOrderDTXCardRepresentation;
import com.mt.saga.appliction.recycle_order_dtx.representation.RecycleOrderDTXRepresentation;
import com.mt.saga.domain.model.distributed_tx.DistributedTx;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "recycleOrderDtx")
public class RecycleOrderDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {

        SumPagedRep<DistributedTx> query = ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(query, RecycleOrderDTXCardRepresentation::new));
    }

    @PostMapping("admin/{dtxId}/cancel")
    public ResponseEntity<?> cancelCreateOrderDtx(
            @PathVariable("dtxId") long dtxId
    ) {
        ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().cancel(dtxId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<DistributedTx> dtx = ApplicationServiceRegistry.getRecycleOrderDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new RecycleOrderDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
