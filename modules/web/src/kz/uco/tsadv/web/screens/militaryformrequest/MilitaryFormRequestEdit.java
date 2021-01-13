package kz.uco.tsadv.web.screens.militaryformrequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.UiComponents;
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
import kz.uco.tsadv.modules.personal.model.MilitaryFormRequest;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv_MilitaryFormRequest.edit")
@UiDescriptor("military-form-request-edit.xml")
@EditedEntityContainer("militaryFormRequestDc")
@LoadDataBeforeShow
public class MilitaryFormRequestEdit extends StandardEditor<MilitaryFormRequest> {

    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<MilitaryFormRequest> militaryFormRequestDc;
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
        if (militaryFormRequestDc.getItem().getAttachments() == null) {
            militaryFormRequestDc.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        militaryFormRequestDc.getItem().getAttachments().add(fd);
    }

    @Subscribe
    protected void onValidation(ValidationEvent event) {
        if (militaryFormRequestDc.getItem().getDate_from().after(militaryFormRequestDc.getItem().getDate_to())) {
            event.getErrors().add(messageBundle.getMessage("expireDateIsOut"));
        }
    }

    public Component generatorName(FileDescriptor fd) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
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