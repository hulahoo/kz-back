package kz.uco.tsadv.web.personaldatarequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.PersonalDataRequest;
import kz.uco.tsadv.service.EmployeeNumberService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PersonalDataRequestEdit extends AbstractEditor<PersonalDataRequest> {

    @Named("fieldGroup.maritalStatus")
    protected LookupPickerField maritalStatusField;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected CommonService commonService;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField addFile;
    @Inject
    protected CollectionDatasource<FileDescriptor, UUID> attachmentsDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    protected Table<FileDescriptor> attachmentTable;
    @Named("attachmentTable.remove")
    protected Action attachmentTableRemove;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        this.setShowSaveNotification(false);
        attachmentsDs.addItemChangeListener(e -> attachmentTableRemove.setEnabled(!attachmentTable.getSelected().isEmpty() && !isDraft()));
    }

    @Override
    protected void initNewItem(PersonalDataRequest item) {
        item.setStatus(commonService.getEntity(DicRequestStatus.class, "DRAFT"));
        super.initNewItem(item);
    }

    @Override
    public void ready() {
        super.ready();
        if (Objects.isNull(getItem().getRequestNumber())) {
            getItem().setRequestNumber(employeeNumberService.generateNextRequestNumber());
        }
        addFile.addFileUploadSucceedListener(this::fileUploadSucceedListener);
    }

    protected void fileUploadSucceedListener(FileUploadField.FileUploadSucceedEvent event) {
        File file = fileUploadingAPI.getFile(addFile.getFileId());
        if (file != null) {
            FileDescriptor fd = addFile.getFileDescriptor();
            try {
                // save file to FileStorage
                fileUploadingAPI.putFileIntoStorage(addFile.getFileId(), fd);
            } catch (FileStorageException e) {
                throw new RuntimeException("Error saving file to FileStorage", e);
            }
            // save file descriptor to database
            attachmentsDs.addItem(fd);
            commit();
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (!isDraft()) {
            fieldGroup.getFields().forEach(f -> f.setEditable(false));
            addFile.setEnabled(false);
            attachmentTableRemove.setEnabled(false);
        }
    }

    protected boolean isDraft() {
        return "DRAFT".equals(getItem().getStatus().getCode());
    }

    public void nextClick() {
        commit();
        if (attachmentsDs.getItems().isEmpty()
                && (getItem().getMaritalStatus() == null || !getItem().getMaritalStatus().getCode().matches("S|single"))) {
            showNotification(getMessage("required.files"), NotificationType.TRAY);
            return;
        }
        AbstractWindow abstractWindow = openEditor("tsadv$PersonalDataRequest.bpm", getItem(), WindowManager.OpenType.DIALOG);
        abstractWindow.addCloseListener(actionId -> close(CLOSE_ACTION_ID));
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (employeeNumberService.hasRequestNumber(getItem().getRequestNumber(), getItem().getId())) {
            errors.add(messages.getMainMessage("errorRequestNum"));
        }
    }

    public void cancelClick() {
        close(CLOSE_ACTION_ID, true);
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

    public void removeFile() {
        attachmentTable.getSelected().forEach(fd -> attachmentsDs.removeItem(fd));
        commit();
        attachmentTableRemove.setEnabled(!attachmentTable.getSelected().isEmpty());
    }
}