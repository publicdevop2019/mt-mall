package com.mt.application.image;

import com.mt.application.ApplicationServiceRegistry;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.domain.DomainRegistry;
import com.mt.domain.image.Image;
import com.mt.domain.image.ImageId;
import com.mt.domain.image.ImageQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ImageApplicationService {
    public static final String IMAGE = "IMAGE";
    @Value("${allowed.types}")
    private List<String> allowedTypes;

    @Value("${allowed.size}")
    private Integer allowedSize;

    @SubscribeForEvent
    @Transactional
    public ImageId create(String changeId, MultipartFile file) {
        ImageId imageId = new ImageId();
        ApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (change) -> {
            Image image = new Image(imageId, file, allowedSize, allowedTypes);
            DomainRegistry.getImageRepository().add(image);
            return null;
        }, IMAGE);
        return imageId;
    }

    public Image queryById(String id) {
        Optional<Image> first = DomainRegistry.getImageRepository().imageOfQuery(new ImageQuery(new ImageId(id))).findFirst();
        if (first.isEmpty())
            throw new IllegalArgumentException("unable to find file on system");
        first.get().loadSource();
        return first.get();
    }
}
