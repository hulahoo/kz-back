package kz.uco.tsadv.web.dicprotectionequipmentphoto;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipmentPhoto;

import javax.inject.Inject;
import java.util.Map;

public class DicProtectionEquipmentPhotoEdit extends AbstractEditor<DicProtectionEquipmentPhoto> {
    @Inject
    protected FileUploadField uploadField;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected Image image;
    @Inject
    protected Datasource<DicProtectionEquipmentPhoto> dicProtectionEquipmentPhotoDs;
    @Inject
    private Button clearImageBtn;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private Button downloadImageBtn;

    @Override
    public void init(Map<String, Object> params) {
        uploadField.addFileUploadSucceedListener(event -> {
            FileDescriptor fd = uploadField.getFileDescriptor();
            try {
                fileUploadingAPI.putFileIntoStorage(uploadField.getFileId(), fd);
            } catch (FileStorageException e) {
                throw new RuntimeException("Error saving file to FileStorage", e);
            }
            getItem().setAttachment(dataSupplier.commit(fd));
            displayImage();
        });

        uploadField.addFileUploadErrorListener(event ->
                showNotification("File upload error", NotificationType.HUMANIZED));

        dicProtectionEquipmentPhotoDs.addItemPropertyChangeListener(event -> {
            if ("attachment".equals(event.getProperty()))
                updateImageButtons(event.getValue() != null);
        });
    }

    @Override
    protected void postInit() {
        displayImage();
        updateImageButtons(getItem().getAttachment() != null);
    }

    public void onDownloadImageBtnClick() {
        if (getItem().getAttachment() != null)
            exportDisplay.show(getItem().getAttachment(), ExportFormat.OCTET_STREAM);
    }

    public void onClearImageBtnClick() {
        getItem().setAttachment(null);
        displayImage();
    }

    private void updateImageButtons(boolean enable) {
        downloadImageBtn.setEnabled(enable);
        clearImageBtn.setEnabled(enable);
    }

    private void displayImage() {
        if (getItem().getAttachment() != null) {
            image.setSource(FileDescriptorResource.class).setFileDescriptor(getItem().getAttachment());
            image.setVisible(true);
        } else {
            image.setVisible(false);
        }
    }
}