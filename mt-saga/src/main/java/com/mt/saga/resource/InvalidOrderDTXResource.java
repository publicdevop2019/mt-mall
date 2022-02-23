package com.mt.saga.resource;

import com.mt.saga.appliction.ApplicationServiceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "invalidOrderDtx")
public class InvalidOrderDTXResource {

    @PostMapping("admin/{dtxId}/cancel")
    public ResponseEntity<?> cancelCreateOrderDtx(
            @PathVariable("dtxId") long dtxId
    ) {
        ApplicationServiceRegistry.getInvalidOrderDTXDTXApplicationService().cancel(dtxId);
        return ResponseEntity.ok().build();
    }

}
