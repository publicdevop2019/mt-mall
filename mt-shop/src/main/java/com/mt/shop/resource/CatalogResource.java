package com.mt.shop.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.catalog.CatalogApplicationService;
import com.mt.shop.application.catalog.command.CreateCatalogCommand;
import com.mt.shop.application.catalog.command.UpdateCatalogCommand;
import com.mt.shop.application.catalog.representation.CatalogCardRepresentation;
import com.mt.shop.application.catalog.representation.CatalogRepresentation;
import com.mt.shop.application.catalog.representation.PublicCatalogCardRepresentation;
import com.mt.shop.domain.model.catalog.Catalog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "catalogs")
public class CatalogResource {

    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<Catalog> catalogs = catalogApplicationService().publicCatalogs(pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep(catalogs, PublicCatalogCardRepresentation::new));
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<CatalogCardRepresentation> catalogs = catalogApplicationService().catalogs(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(catalogs);
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", catalogApplicationService().create(command, changeId)).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") String id, @RequestBody UpdateCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") String id) {
        Optional<Catalog> catalog = catalogApplicationService().catalog(id);
        return catalog.map(value -> ResponseEntity.ok(new CatalogRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogApplicationService().removeCatalog(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") String id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogApplicationService().patch(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    private CatalogApplicationService catalogApplicationService() {
        return ApplicationServiceRegistry.getCatalogApplicationService();
    }
}
