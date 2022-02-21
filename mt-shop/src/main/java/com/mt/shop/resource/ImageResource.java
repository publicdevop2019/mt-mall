package com.mt.shop.resource;

import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.domain.model.image.Image;
import com.mt.shop.domain.model.image.ImageId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

@Slf4j
@RestController
@RequestMapping
public class ImageResource {

    public static final String CONTENT_TYPE = "content-type";
    public static final String LOCATION = "Location";

    @GetMapping("files/public/{id}")
    public ResponseEntity<byte[]> getUploadedFileById(@PathVariable(name = "id") String id) {
        Image image = ApplicationServiceRegistry.getImageApplicationService().queryById(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE,
                image.getContentType());
        responseHeaders.setContentDispositionFormData(image.getOriginalName(), image.getOriginalName());
        return ResponseEntity.ok().headers(responseHeaders).body(image.getSource());
    }

    @PostMapping("files/app")
    public ResponseEntity<?> uploadedFiles(@RequestParam("file") MultipartFile file, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ImageId imageId = ApplicationServiceRegistry.getImageApplicationService().create(changeId, file);
        return ResponseEntity.ok().header(LOCATION, imageId.getDomainId()).build();
    }


}
