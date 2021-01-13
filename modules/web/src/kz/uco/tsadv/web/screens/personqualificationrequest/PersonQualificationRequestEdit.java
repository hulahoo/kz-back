package kz.uco.tsadv.web.screens.personqualificationrequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.tb.PersonQualificationRequest;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv_PersonQualificationRequest.edit")
@UiDescriptor("person-qualification-request-edit.xml")
@EditedEntityContainer("personQualificationRequestDc")
@LoadDataBeforeShow
public class PersonQualificationRequestEdit extends StandardEditor<PersonQualificationRequest> {

    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<PersonQualificationRequest> personQualificationRequestDc;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> attachmentsDc;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private ComponentsFactory componentsFactory;

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
        if (personQualificationRequestDc.getItem().getAttachments() == null) {
            personQualificationRequestDc.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        personQualificationRequestDc.getItem().getAttachments().add(fd);
    }

    @Subscribe
    protected void onValidation(ValidationEvent event) {
        if (personQualificationRequestDc.getItem().getStartDate() != null
                && personQualificationRequestDc.getItem().getEndDate() != null
                && personQualificationRequestDc.getItem().getStartDate()
                .after(personQualificationRequestDc.getItem().getEndDate())) {
            event.getErrors().add(messageBundle.getMessage("expireDateIsOut"));
        }
        if (personQualificationRequestDc.getItem().getAttachments() == null
                || personQualificationRequestDc.getItem().getAttachments().size() == 0) {
            event.getErrors().add(messageBundle.getMessage("downloadOneFile"));
        }
    }

    public Component generatorName(FileDescriptor fd) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(fd.getName());
        linkButton.setAction(new BaseAction("export") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                exportDisplay.show(fd, ExportFormat.OCTET_STREAM);
            }
        });
        return linkButton;
    }
}