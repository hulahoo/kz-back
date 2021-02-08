package kz.uco.tsadv.web.screens.disabilityrequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.model.DisabilityRequest;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv_DisabilityRequest.edit")
@UiDescriptor("disability-request-edit.xml")
@EditedEntityContainer("disabilityRequestDc")
@LoadDataBeforeShow
public class DisabilityRequestEdit extends StandardEditor<DisabilityRequest> {

    @Inject
    protected TextField<String> groupField;
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
    protected InstanceContainer<DisabilityRequest> disabilityRequestDc;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> attachmentsDc;

    @Subscribe("haveDisabilityField")
    protected void onHaveDisabilityFieldValueChange(HasValue.ValueChangeEvent<YesNoEnum> event) {
        groupField.setRequired(event.getValue() != null && event.getValue().equals(YesNoEnum.YES));
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
        if (disabilityRequestDc.getItem().getAttachments() == null) {
            disabilityRequestDc.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        disabilityRequestDc.getItem().getAttachments().add(fd);
    }
}