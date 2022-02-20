package com.mt.domain;

import com.mt.domain.image.ImageRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static ImageRepository imageRepository;
    @Autowired
    private void setImageRepository(ImageRepository imageRepository){
        DomainRegistry.imageRepository=imageRepository;
    }
}
