package kz.uco.tsadv.web.screens.personeducationrequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.recruitment.model.PersonEducationRequest;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv_PersonEducationRequest.edit")
@UiDescriptor("person-education-request-edit.xml")
@EditedEntityContainer("personEducationRequestDc")
@LoadDataBeforeShow
public class PersonEducationRequestEdit extends StandardEditor<PersonEducationRequest> {
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected InstanceContainer<PersonEducationRequest> personEducationRequestDc;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> attachmentsDc;
    @Inject
    protected MessageBundle messageBundle;

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
        if (personEducationRequestDc.getItem().getAttachments() == null) {
            personEducationRequestDc.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        personEducationRequestDc.getItem().getAttachments().add(fd);
    }

    @Subscribe
    protected void onValidation(ValidationEvent event) {
        if (personEducationRequestDc.getItem().getStartYear() != null &&
                personEducationRequestDc.getItem().getEndYear() != null &&
                personEducationRequestDc.getItem().getStartYear()
                        .compareTo(personEducationRequestDc.getItem().getEndYear()) >= 0) {
            event.getErrors().add(messageBundle.getMessage("wrongEducationYears"));
        }
    }
}