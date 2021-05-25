package kz.uco.tsadv.web.screens.persondocumentrequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicApprovalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.model.PersonDocumentRequest;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv_PersonDocumentRequest.edit")
@UiDescriptor("person-document-request-edit.xml")
@EditedEntityContainer("personDocumentRequestDc")
@LoadDataBeforeShow
public class PersonDocumentRequestEdit extends StandardEditor<PersonDocumentRequest> {

    @Inject
    protected CollectionLoader<DicDocumentType> dicDocumentTypesDl;
    @Inject
    protected CommonService commonService;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> attachmentsDc;
    @Inject
    protected InstanceContainer<PersonDocumentRequest> personDocumentRequestDc;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private ExportDisplay exportDisplay;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (!((MapScreenOptions) event.getOptions()).getParams().containsKey("isForeigner")) {
            dicDocumentTypesDl.setQuery("select e from tsadv$DicDocumentType e");
        } else {
            dicDocumentTypesDl.setParameter("isForeigner", ((MapScreenOptions) event.getOptions())
                    .getParams().get("isForeigner"));
        }
    }


    @Subscribe
    protected void onInitEntity(InitEntityEvent<PersonDocumentRequest> event) {
//        event.getEntity().setStatus(commonService.getEntity(DicApprovalStatus.class, "PROJECT"));
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
        if (personDocumentRequestDc.getItem().getAttachments() == null) {
            personDocumentRequestDc.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        personDocumentRequestDc.getItem().getAttachments().add(fd);
    }

    @Subscribe
    protected void onValidation(ValidationEvent event) {
        if (personDocumentRequestDc.getItem().getIssueDate().after(personDocumentRequestDc.getItem().getExpiredDate())) {
            event.getErrors().add(messageBundle.getMessage("expireDateIsOut"));
        }
        if (personDocumentRequestDc.getItem().getAttachments() == null
                || personDocumentRequestDc.getItem().getAttachments().size() == 0) {
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