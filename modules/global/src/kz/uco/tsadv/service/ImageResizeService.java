package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.tsadv.entity.ImageSize;

import java.io.IOException;

public interface ImageResizeService {
    String NAME = "tsadv_ImageResizeService";

    FileDescriptor resizeImage(ImageSize size, FileDescriptor originalImage);
}