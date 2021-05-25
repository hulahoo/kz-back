package kz.uco.tsadv.service;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.ImageSize;
import kz.uco.tsadv.entity.ResizedImage;
import kz.uco.tsadv.listener.BudgetRequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;

@Service(ImageResizeService.NAME)
public class ImageResizeServiceBean implements ImageResizeService {

    private static final Logger log = LoggerFactory.getLogger(BudgetRequestListener.class);

    @Inject
    private FileStorageAPI fileStorageAPI;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private Persistence persistence;

    @Override
    public FileDescriptor resizeImage(ImageSize size, FileDescriptor originalImage) {
        return findResizedImage(originalImage, size)
                .orElseGet(() -> createResizedImage(size, originalImage));
    }

    private FileDescriptor createResizedImage(ImageSize size, FileDescriptor originalImage) {
        return persistence.callInTransaction(em -> {
            try {
                final InputStream fileIs = fileStorageAPI.openStream(originalImage);
                BufferedImage sourceImage = ImageIO.read(fileIs);
                Image thumbnail = sourceImage.getScaledInstance(size.getWidth(), size.getHeight(), Image.SCALE_DEFAULT);
                BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),
                        thumbnail.getHeight(null),
                        BufferedImage.TYPE_INT_RGB);
                bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedThumbnail, originalImage.getExtension(), baos);

                final ByteArrayInputStream newImageByteArray = new ByteArrayInputStream(baos.toByteArray());

                final FileDescriptor newImage = metadata.create(FileDescriptor.class);
                newImage.setExtension(originalImage.getExtension());
                newImage.setName(String.format("%s_w%d_h%d", originalImage.getName(), size.getWidth(), size.getHeight()));
                newImage.setCreateDate(new Date());
                newImage.setSize((long) baos.size());

                em.persist(newImage);

                fileStorageAPI.saveStream(newImage, newImageByteArray);

                final ResizedImage newResizedImage = metadata.create(ResizedImage.class);
                newResizedImage.setResizedImage(newImage);
                newResizedImage.setOriginalImage(originalImage);
                newResizedImage.setSize(size);

                em.persist(newResizedImage);

                return newImage;
            } catch (FileStorageException | IOException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        });
    }

    private Optional<FileDescriptor> findResizedImage(FileDescriptor originalImage, ImageSize size) {
        return dataManager.load(FileDescriptor.class)
                .query("" +
                        "select ri.resizedImage " +
                        "from tsadv_ResizedImage ri " +
                        "where ri.originalImage.id = :originalImageId " +
                        "   and ri.size.id = :sizeId ")
                .view(View.MINIMAL)
                .parameter("originalImageId", originalImage.getId())
                .parameter("sizeId", size.getId())
                .optional();
    }
}