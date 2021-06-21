package kz.uco.tsadv.web.addressrequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.model.AddressRequest;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv$AddressRequest.edit")
@UiDescriptor("address-request-edit.xml")
@EditedEntityContainer("addressRequestDs")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT)
        }
)
public class AddressRequestEdit extends AbstractBprocEditor<AddressRequest> {

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private Form form;
    @Inject
    private InstanceContainer<AddressRequest> addressRequestDs;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private FileUploadField upload;
    @Inject
    private CollectionPropertyContainer<FileDescriptor> attachmentsDc;

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
        form.setEditable(isDraft());
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
        if (addressRequestDs.getItem().getAttachments() == null) {
            addressRequestDs.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        addressRequestDs.getItem().getAttachments().add(fd);
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