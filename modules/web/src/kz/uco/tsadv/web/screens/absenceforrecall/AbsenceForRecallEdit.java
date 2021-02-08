package kz.uco.tsadv.web.screens.absenceforrecall;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.Notifications;
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
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.model.AbsenceForRecall;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;

@UiController("tsadv_AbsenceForRecall.edit")
@UiDescriptor("absence-for-recall-edit.xml")
@EditedEntityContainer("absenceForRecallDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT)
        }
)

public class AbsenceForRecallEdit extends AbstractBprocEditor<AbsenceForRecall> {
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<AbsenceForRecall> absenceForRecallDc;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> fileDc;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected DateField<Date> dateFromField;
    @Inject
    protected DateField<Date> dateToField;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected DatesService datesService;

    @Subscribe("upload")
    protected void onUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        FileDescriptor fd = upload.getFileDescriptor();
        try {
            fileUploadingAPI.putFileIntoStorage(upload.getFileId(), fd);
        } catch (FileStorageException e) {
            throw new RuntimeException("Error saving file to FileStorage", e);
        }
        dataManager.commit(fd);
        if (absenceForRecallDc.getItem().getFile() == null) {
            absenceForRecallDc.getItem().setFile(new ArrayList<FileDescriptor>());
        }
        fileDc.getDisconnectedItems().add(fd);
        absenceForRecallDc.getItem().getFile().add(fd);
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

    @Subscribe("leaveOtherTimeField")
    protected void onLeaveOtherTimeFieldValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (event.getValue() != null && event.getValue().equals(true)) {
            dateFromField.setEditable(true);
            dateToField.setEditable(true);
            dateFromField.setRequired(true);
            dateToField.setRequired(true);
            absenceForRecallDc.getItem().setCompensationPayment(false);
        } else {
            dateFromField.setEditable(false);
            dateToField.setEditable(false);
            dateFromField.setRequired(false);
            dateToField.setRequired(false);
            absenceForRecallDc.getItem().setDateFrom(null);
            absenceForRecallDc.getItem().setDateTo(null);
            absenceForRecallDc.getItem().setCompensationPayment(true);
        }
    }

    @Subscribe("compensationPaymentField")
    protected void onCompensationPaymentFieldValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (event.getValue() != null && event.getValue().equals(true)) {
            dateFromField.setEditable(false);
            dateToField.setEditable(false);
            absenceForRecallDc.getItem().setLeaveOtherTime(false);
        } else {
            absenceForRecallDc.getItem().setLeaveOtherTime(true);
        }
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
    }

    @Nullable
    @Override
    protected TsadvUser getEmployee() {
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup = :personGroup")
                .parameter("personGroup", absenceForRecallDc.getItem().getEmployee())
                .view("userExt.edit")
                .list().stream().findFirst().orElse(null);
    }

    @Override
    protected void startProcess(Action.ActionPerformedEvent event) {
        AbsenceForRecall item = absenceForRecallDc.getItem();
        if (item.getRecallDateFrom() != null && item.getRecallDateTo() != null) {
            if (item.getDateFrom() != null && item.getDateTo() != null) {
                if (!areDaysCorrect(item)) {
                    notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                            .withCaption(messageBundle.getMessage("daysNotCorrect")).show();
                    return;
                }
            }
            if (item.getRecallDateFrom().before(item.getVacation().getDateFrom())
                    || item.getRecallDateTo().after(item.getVacation().getDateTo())
                    || item.getRecallDateFrom().after(item.getRecallDateTo())) {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("recallDateNotCorrect")).show();
            } else {
                super.startProcess(event);
            }
        }
    }

    private boolean areDaysCorrect(AbsenceForRecall item) {
        int shouldBe = datesService.getFullDaysCount(item.getRecallDateFrom(), item.getRecallDateTo());
        int thereIs = datesService.getFullDaysCount(item.getDateFrom(), item.getDateTo());
        return shouldBe >= thereIs;
    }
}