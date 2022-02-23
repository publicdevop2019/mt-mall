package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.representation.CancelConfirmOrderPaymentDTXCardRepresentation;
import com.mt.saga.appliction.cancel_confirm_order_payment_dtx.representation.CancelConfirmOrderPaymentDTXRepresentation;
import com.mt.saga.appliction.common.ResolveReason;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "cancelConfirmOrderPaymentDtx")
public class CancelConfirmOrderPaymentDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<CancelConfirmOrderPaymentDTX> dtx = ApplicationServiceRegistry.getCancelConfirmOrderPaymentDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, CancelConfirmOrderPaymentDTXCardRepresentation::new));
    }
    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<CancelConfirmOrderPaymentDTX> dtx = ApplicationServiceRegistry.getCancelConfirmOrderPaymentDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new CancelConfirmOrderPaymentDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("admin/{id}/resolve")
    public ResponseEntity<?> resolveById(
            @PathVariable(name = "id") long id,
            @RequestBody ResolveReason resolveReason
    ) {
        ApplicationServiceRegistry.getCancelConfirmOrderPaymentDTXApplicationService().resolve(id,resolveReason);
        return ResponseEntity.ok().build();
    }
}
