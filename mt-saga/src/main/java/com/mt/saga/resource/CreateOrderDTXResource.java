package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.create_order_dtx.representation.CreateOrderDTXCardRepresentation;
import com.mt.saga.appliction.create_order_dtx.representation.CreateOrderDTXRepresentation;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "createOrderDtx")
public class CreateOrderDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<CreateOrderDTX> dtx = ApplicationServiceRegistry.getCreateOrderDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, CreateOrderDTXCardRepresentation::new));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<CreateOrderDTX> dtx = ApplicationServiceRegistry.getCreateOrderDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new CreateOrderDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("admin/{dtxId}/cancel")
    public ResponseEntity<?> cancelCreateOrderDtx(
            @PathVariable("dtxId") long dtxId
    ) {
        ApplicationServiceRegistry.getCreateOrderDTXApplicationService().cancel(dtxId);
        return ResponseEntity.ok().build();
    }

}
