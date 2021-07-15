package kz.uco.tsadv.entity;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_RESIZED_IMAGE")
@Entity(name = "tsadv_ResizedImage")
public class ResizedImage extends StandardEntity {
    private static final long serialVersionUID = 5364762537029601183L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SIZE_ID")
    private ImageSize size;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORIGINAL_IMAGE_ID")
    private FileDescriptor originalImage;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RESIZED_IMAGE_ID")
    private FileDescriptor resizedImage;

    public FileDescriptor getResizedImage() {
        return resizedImage;
    }

    public void setResizedImage(FileDescriptor resizedImage) {
        this.resizedImage = resizedImage;
    }

    public FileDescriptor getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(FileDescriptor originalImage) {
        this.originalImage = originalImage;
    }

    public ImageSize getSize() {
        return size;
    }

    public void setSize(ImageSize size) {
        this.size = size;
    }
}