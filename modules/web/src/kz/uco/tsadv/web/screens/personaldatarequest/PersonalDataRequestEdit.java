package kz.uco.tsadv.web.screens.personaldatarequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.personal.model.PersonalDataRequest;

import javax.inject.Inject;
import java.io.File;

@UiController("tsadv$PersonalDataRequest.edit")
@UiDescriptor("personal-data-request-edit.xml")
@EditedEntityContainer("personalDataRequestDc")
@LoadDataBeforeShow
public class PersonalDataRequestEdit extends StandardEditor<PersonalDataRequest> {
    @Inject
    protected InstanceContainer<PersonalDataRequest> personalDataRequestDc;
    @Inject
    protected FileLoader fileLoader;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
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
        attachmentsDc.getDisconnectedItems().add(fd);
        personalDataRequestDc.getItem().getAttachments().add(fd);

    }


}