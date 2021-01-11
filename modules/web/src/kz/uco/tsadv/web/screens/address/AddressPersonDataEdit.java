package kz.uco.tsadv.web.screens.address;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.personal.model.Address;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv$AddressPersonData.edit")
@UiDescriptor("address-person-data-edit.xml")
@EditedEntityContainer("addressDc")
@LoadDataBeforeShow
public class AddressPersonDataEdit extends StandardEditor<Address> {

    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<Address> addressDc;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> attachmentsDc;

    @Subscribe("upload")
    protected void onUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        File file = fileUploadingAPI.getFile(upload.getFileId());
        FileDescriptor fd = upload.getFileDescriptor();
        try {
            fileUploadingAPI.putFileIntoStorage(upload.getFileId(), fd);
        } catch (FileStorageException e) {
            throw new RuntimeException("Error saving file to FileStorage", e);
        }
        dataManager.commit(fd);
        if (addressDc.getItem().getAttachments() == null) {
            addressDc.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        addressDc.getItem().getAttachments().add(fd);
    }
}