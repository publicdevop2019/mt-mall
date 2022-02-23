package com.mt.saga.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.saga.appliction.ApplicationServiceRegistry;
import com.mt.saga.appliction.confirm_order_payment_dtx.representation.ConfirmOrderPaymentDTXCardRepresentation;
import com.mt.saga.appliction.confirm_order_payment_dtx.representation.ConfirmOrderPaymentDTXRepresentation;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "confirmOrderPaymentDtx")
public class ConfirmOrderPaymentDTXResource {
    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<ConfirmOrderPaymentDTX> dtx = ApplicationServiceRegistry.getConfirmOrderPaymentDTXApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(dtx, ConfirmOrderPaymentDTXCardRepresentation::new));
    }
    @PostMapping("admin/{dtxId}/cancel")
    public ResponseEntity<?> cancelCreateOrderDtx(
            @PathVariable("dtxId") long dtxId
    ) {
        ApplicationServiceRegistry.getConfirmOrderPaymentDTXApplicationService().cancel(dtxId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(
            @PathVariable(name = "id") long id
    ) {
        Optional<ConfirmOrderPaymentDTX> dtx = ApplicationServiceRegistry.getConfirmOrderPaymentDTXApplicationService().query(id);
        return dtx.map(e -> ResponseEntity.ok(new ConfirmOrderPaymentDTXRepresentation(e))).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
