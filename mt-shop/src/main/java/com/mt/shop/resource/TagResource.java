package com.mt.shop.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.tag.TagApplicationService;
import com.mt.shop.application.tag.command.CreateTagCommand;
import com.mt.shop.application.tag.command.UpdateTagCommand;
import com.mt.shop.application.tag.representation.TagCardRepresentation;
import com.mt.shop.application.tag.representation.TagRepresentation;
import com.mt.shop.domain.model.tag.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "attributes")
public class TagResource {

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<Tag> tags = tagApplicationService().tags(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep(tags, TagCardRepresentation::new));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") String id) {
        Optional<Tag> tag = tagApplicationService().tag(id);
        return tag.map(value -> ResponseEntity.ok(new TagRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateTagCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", tagApplicationService().create(command, changeId)).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") String id, @RequestBody UpdateTagCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        tagApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        tagApplicationService().removeById(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") String id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        tagApplicationService().patch(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    private TagApplicationService tagApplicationService() {
        return ApplicationServiceRegistry.getTagApplicationService();
    }
}
