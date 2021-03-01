package kz.uco.tsadv.web.screens.changeabsencedaysrequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicPurposeAbsence;
import kz.uco.tsadv.modules.personal.model.ChangeAbsenceDaysRequest;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;

@UiController("tsadv_ChangeAbsenceDaysRequest.edit")
@UiDescriptor("change-absence-days-request-edit.xml")
@EditedEntityContainer("changeAbsenceDaysRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT)
        }
)
public class ChangeAbsenceDaysRequestEdit extends AbstractBprocEditor<ChangeAbsenceDaysRequest> {

    @Inject
    protected FileUploadField upload;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected InstanceContainer<ChangeAbsenceDaysRequest> changeAbsenceDaysRequestDc;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> fileDc;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected TextArea<String> purposeTextField;
    @Inject
    protected DatesService datesService;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected CheckBox agreeField;
    @Inject
    protected CheckBox familiarizationField;
    @Inject
    protected PickerField<DicPurposeAbsence> purposeField;
    @Inject
    protected DateField<Date> newStartDateField;
    @Inject
    protected DateField<Date> newEndDateField;
    @Inject
    protected DateField<Date> periodStartDateField;
    @Inject
    protected DateField<Date> periodEndDateField;
    @Named("fileTable.remove")
    protected RemoveAction<FileDescriptor> fileTableRemove;

    @Subscribe("upload")
    protected void onUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        FileDescriptor fd = upload.getFileDescriptor();
        try {
            fileUploadingAPI.putFileIntoStorage(upload.getFileId(), fd);
        } catch (FileStorageException e) {
            throw new RuntimeException("Error saving file to FileStorage", e);
        }
        dataManager.commit(fd);
        if (changeAbsenceDaysRequestDc.getItem().getFile() == null) {
            changeAbsenceDaysRequestDc.getItem().setFile(new ArrayList<>());
        }
        fileDc.getDisconnectedItems().add(fd);
        changeAbsenceDaysRequestDc.getItem().getFile().add(fd);
    }

    public Component generatorName(FileDescriptor fd) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(fd.getName());
        linkButton.setAction(new BaseAction("export")
                .withHandler(actionPerformedEvent -> exportDisplay.show(fd, ExportFormat.OCTET_STREAM)));
        return linkButton;
    }

    @Override
    protected void onAfterShow(AfterShowEvent event) {
        super.onAfterShow(event);
        if (changeAbsenceDaysRequestDc.getItem().getPurpose() != null
                && "OTHER".equals(changeAbsenceDaysRequestDc.getItem().getPurpose().getCode())) {
            purposeTextField.setVisible(true);
        }
        if (changeAbsenceDaysRequestDc.getItem().getStatus() != null
                && !changeAbsenceDaysRequestDc.getItem().getStatus().getCode().equals("DRAFT")) {
            purposeField.setEditable(false);
            purposeTextField.setEditable(false);
            newStartDateField.setEditable(false);
            newEndDateField.setEditable(false);
            periodStartDateField.setEditable(false);
            periodEndDateField.setEditable(false);
            upload.setEditable(false);
            fileTableRemove.setEnabled(false);
        }
    }

    @Subscribe("purposeField")
    protected void onPurposeFieldValueChange(HasValue.ValueChangeEvent<DicPurposeAbsence> event) {
        if (event.getValue() != null && "OTHER".equals(event.getValue().getCode())) {
            purposeTextField.setVisible(true);
        } else {
            purposeTextField.setVisible(false);
            purposeTextField.setValue(null);
        }
    }

    @Subscribe("newStartDateField")
    protected void onNewStartDateFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (event.getValue() != null && changeAbsenceDaysRequestDc.getItem().getNewEndDate() != null) {
            if (!areDaysCorrect(changeAbsenceDaysRequestDc.getItem())) {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("daysNotCorrect"))
                        .show();
            }
        }
    }

    @Subscribe("newEndDateField")
    protected void onNewEndDateFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (event.getValue() != null && changeAbsenceDaysRequestDc.getItem().getNewStartDate() != null) {
            if (!areDaysCorrect(changeAbsenceDaysRequestDc.getItem())) {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("daysNotCorrect"))
                        .show();
            }
        }
    }

    private boolean areDaysCorrect(ChangeAbsenceDaysRequest item) {
        int shouldBe = datesService.getFullDaysCount(item.getScheduleStartDate(), item.getScheduleEndDate());
        int thereIs = datesService.getFullDaysCount(item.getNewStartDate(), item.getNewEndDate());
        return shouldBe >= thereIs;
    }

    @Override
    protected void startProcess(Action.ActionPerformedEvent event) {
        if (!areDaysCorrect(changeAbsenceDaysRequestDc.getItem())) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("daysNotCorrect"))
                    .show();
            return;
        }
        super.startProcess(event);
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();

        if (isApproverEmployee()) {
            agreeField.setEditable(true);
            familiarizationField.setEditable(true);
        }
    }

    private boolean isApproverEmployee() {
        ExtTaskData activeTaskData = (ExtTaskData) this.activeTaskData;
        if (activeTaskData != null) {
            return activeTaskData.getAssigneeOrCandidates().contains(userSession.getUser())
                    && activeTaskData.getHrRole() != null
                    && activeTaskData.getHrRole().getCode() != null
                    && activeTaskData.getHrRole().getCode().equals("EMPLOYEE");
        }
        return false;
    }

    @Nullable
    @Override
    protected TsadvUser getEmployee() {
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup = :personGroup")
                .parameter("personGroup", changeAbsenceDaysRequestDc.getItem().getEmployee())
                .view("userExt.edit")
                .list().stream().findFirst().orElse(null);
    }
}