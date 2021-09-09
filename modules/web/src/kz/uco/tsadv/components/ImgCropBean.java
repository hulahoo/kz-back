package kz.uco.tsadv.components;

import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.components.StreamResource;
import com.haulmont.cuba.gui.screen.FrameOwner;
import cubacn.cmp.crop.web.screens.imgcrop.ImageCropWindow;
import cubacn.cmp.crop.web.screens.imgcrop.ImageCropWindowOptions;
import cubacn.cmp.crop.web.toolkit.ui.imgcrop.ImgCropServerComponent;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;

import static com.haulmont.cuba.gui.screen.FrameOwner.WINDOW_COMMIT_AND_CLOSE_ACTION;
import static com.haulmont.cuba.gui.screen.FrameOwner.WINDOW_DISCARD_AND_CLOSE_ACTION;

@Component(ImgCropBean.NAME)
public class ImgCropBean {
    public static final String NAME = "tsadv_ImgCropBean";

    protected File getFile(InputStream inputStream) {
        File file = null;
        try {
            file = File.createTempFile("prefix1", "suffix1");
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            // handle exception here
        } catch (IOException e) {
            // handle exception here
        }
        return file;
    }

    public void crop(FrameOwner origin, InputStream inputStream, Image image, FileUploadField fileUploadField) {
        crop(origin, getFile(inputStream), image, fileUploadField);
    }

    public void crop(FrameOwner origin, File file, Image image, FileUploadField fileUploadField) {
        // Create an viewport configuration object
        ImgCropServerComponent.ViewPort viewPort =
                new ImgCropServerComponent.ViewPort(300, 200,
                        ImgCropServerComponent.ViewPortType.square);
        // Create an option object
        ImageCropWindowOptions options = new ImageCropWindowOptions(file, 10, viewPort);
        // Open a winow for cropping an image
        ImageCropWindow.showAsDialog(origin, options, (cropWindowAfterScreenCloseEvent) -> {
            // process the cropping result
            if (cropWindowAfterScreenCloseEvent.getCloseAction().equals(WINDOW_DISCARD_AND_CLOSE_ACTION)) {
                //cropping window is closed by  "Cancel" button
            } else if (cropWindowAfterScreenCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION)) {
                // cropping window is closed  by "ok" button,then we can get the cropping result in bytes.
                byte[] result = options.getResult();
                if (result != null) {

                    image.setSource(StreamResource.class)
                            .setStreamSupplier(() -> new ByteArrayInputStream(result)).setBufferSize(1024);

                    fileUploadField.setContentProvider(() -> new ByteArrayInputStream(result));
                }
            }
        });
    }
}