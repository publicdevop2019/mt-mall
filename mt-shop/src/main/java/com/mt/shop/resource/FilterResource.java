package com.mt.shop.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.filter.FilterApplicationService;
import com.mt.shop.application.filter.command.CreateFilterCommand;
import com.mt.shop.application.filter.command.UpdateFilterCommand;
import com.mt.shop.application.filter.representation.FilterCardRepresentation;
import com.mt.shop.application.filter.representation.FilterRepresentation;
import com.mt.shop.application.filter.representation.PublicFilterCardRepresentation;
import com.mt.shop.domain.model.filter.Filter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.*;


@RestController
@RequestMapping(produces = "application/json", path = "filters")
public class FilterResource {
    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<Filter> filterSumPagedRep = filterApplicationService().publicFilters(queryParam, pageParam, skipCount);
        List<Filter> data = filterSumPagedRep.getData();
        List<PublicFilterCardRepresentation> collect = null;
        if (data.size() != 0) {
            collect = filterSumPagedRep.getData().get(0).getFilterItems().stream().map(PublicFilterCardRepresentation::new).collect(Collectors.toList());
        }
        return ResponseEntity.ok(new SumPagedRep<>(collect, null));
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<FilterCardRepresentation> filters = filterApplicationService().filters(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(filters);
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") String id) {
        Optional<Filter> filter = filterApplicationService().filter(id);
        return filter.map(value -> ResponseEntity.ok(new FilterRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateFilterCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", filterApplicationService().create(command, changeId)).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdmin(@PathVariable(name = "id") String id, @RequestBody UpdateFilterCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        filterApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        filterApplicationService().removeFilter(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") String id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        filterApplicationService().patch(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    private FilterApplicationService filterApplicationService() {
        return ApplicationServiceRegistry.getFilterApplicationService();
    }
}
