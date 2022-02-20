package com.mt.application;

import com.mt.application.image.ImageApplicationService;
import com.mt.common.domain.model.idempotent.IdempotentService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Getter
    private static ImageApplicationService imageApplicationService;
    @Getter
    private static IdempotentService idempotentService;

    @Autowired
    private void setIdempotentService(IdempotentService idempotentService) {
        ApplicationServiceRegistry.idempotentService = idempotentService;
    }
    @Autowired
    private void setImageApplicationService(ImageApplicationService imageApplicationService) {
        ApplicationServiceRegistry.imageApplicationService = imageApplicationService;
    }
}
