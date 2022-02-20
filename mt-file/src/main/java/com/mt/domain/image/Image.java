package com.mt.domain.image;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

@Entity
@Table(name = "uploaded_file")
@Data
@NoArgsConstructor
@Slf4j
public class Image extends Auditable {

    @Id
    private Long id;

    @Column
    private String systemPath;

    @Embedded
    private ImageId imageId;
    @Setter
    @Getter
    private transient byte[] source;
    @Column
    private String originalName;

    @Column
    private String contentType;

    public Image(ImageId id, MultipartFile file, Integer allowedSize, List<String> allowedTypes) {
        validateUploadCriteria(file, allowedSize, allowedTypes);
        String path = "files/" + id + ".upload";
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.contentType = file.getContentType();
        this.originalName = file.getOriginalFilename();
        this.systemPath = path;
        this.imageId = id;
        File targetFile = new File(path);
        File dir = new File("files");
        if (!dir.exists()) {
            dir.mkdir();
        }
        OutputStream outStream;
        try {
            targetFile.createNewFile();
            outStream = new FileOutputStream(targetFile, false);
            outStream.write((file.getBytes()));
        } catch (IOException e) {
            log.error("error during saving file", e);
            throw new FileUploadException();
        }
    }

    /**
     * validate file type, file size
     */
    private void validateUploadCriteria(MultipartFile file, Integer allowedSize, List<String> allowedTypes) {
        if (allowedTypes.stream().noneMatch(e -> e.equals(file.getContentType())))
            throw new FileTypeException();
        try {
            if (file.getBytes().length > allowedSize)
                throw new FileSizeException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSource() {
        File fi = new File(this.getSystemPath());
        try {
            this.source = Files.readAllBytes(fi.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("unable to find target file binary on system");
        }
    }
}
