package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.model.AbsenceRvdRequest;
import kz.uco.tsadv.modules.personal.model.DicShift;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.inject.Inject;
import java.io.File;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@UiController("tsadv_AbsenceRvdRequest.edit")
@UiDescriptor("absence-rvd-request-edit.xml")
@EditedEntityContainer("absenceRvdRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT),
                @Outcome(id = AbstractBprocRequest.OUTCOME_CANCEL)
        }
)
public class AbsenceRvdRequestEdit extends AbstractBprocEditor<AbsenceRvdRequest> {
    @Inject
    protected DateField<Date> timeOfStartingField;
    @Inject
    protected InstanceContainer<AbsenceRvdRequest> absenceRvdRequestDc;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> filesDc;
    @Inject
    protected DateField<Date> timeOfFinishingField;
    protected boolean isBlocked = false;
    @Inject
    protected CheckBox vacationDayField;
    @Inject
    protected TextField<String> purposeTextField;
    @Inject
    protected CheckBox compensationField;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected FileUploadField addFile;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;

    protected ExtTaskData extTaskData;
    @Inject
    protected Form form;
    @Inject
    protected PickerField<DicShift> shiftField;
    @Inject
    protected TextField<String> shiftCodeField;
    @Inject
    protected TextField<YesNoEnum> overrideAllHoursByDayField;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        if (!isDraft()) {
            initEditableFields();
        }
        if (absenceRvdRequestDc.getItem().getType() != null
                && absenceRvdRequestDc.getItem().getType().getWorkOnWeekend()) {
            compensationField.setVisible(true);
            vacationDayField.setVisible(true);
            shiftField.setVisible(true);
        } else if (absenceRvdRequestDc.getItem().getType() != null
                && absenceRvdRequestDc.getItem().getType().getTemporaryTransfer()) {
            shiftCodeField.setVisible(true);
            shiftField.setVisible(true);
            overrideAllHoursByDayField.setVisible(true);
        }
    }

    @Override
    protected void initEditableFields() {
        extTaskData = (ExtTaskData) getActiveTaskData();
        if (extTaskData.getHrRole() != null
                && !"HR".equalsIgnoreCase(extTaskData.getHrRole().getCode())) {
            form.setEditable(false);
        }
    }

    @Subscribe
    protected void onInitEntity(InitEntityEvent<AbsenceRvdRequest> event) {
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
            filesDc.getMutableItems().add(dataManager.commit(fd));
        }
    }

    @Subscribe(id = "absenceRvdRequestDc", target = Target.DATA_CONTAINER)
    protected void onAbsenceRvdRequestDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<AbsenceRvdRequest> event) {
        if (!isBlocked) {
            isBlocked = true;
            if (event.getProperty().equals("compensation")) {
                absenceRvdRequestDc.getItem().setVacationDay(!event.getItem().getCompensation());
            } else if (event.getProperty().equals("vacationDay")) {
                absenceRvdRequestDc.getItem().setCompensation(!event.getItem().getVacationDay());
            }
            isBlocked = false;
        }
        if (event.getProperty().equals("type")) {
            if (event.getItem().getType() != null) {
                String code = event.getItem().getType().getCode();
                if (code != null) {
                    setTimesNull();
                    if (code.equals("WORK_ON_WEEKEND") || code.equals("SUPERTIME_WORK")) {
                        changeHint(false);
                    } else changeHint(code.equals("TEMPORARY_TRANSFER"));
                }
            }
        } else if (event.getProperty().equals("purpose")) {
            purposeTextField.setVisible(event.getItem().getPurpose() != null
                    && event.getItem().getPurpose().getCode() != null
                    && event.getItem().getPurpose().getCode().equals("OTHER"));
        } else if (event.getProperty().equals("timeOfStarting") || event.getProperty().equals("timeOfFinishing")) {
            if (event.getItem().getTimeOfFinishing() != null && event.getItem().getTimeOfStarting() != null) {
                Duration between = Duration.between(
                        convertToLocalDateTimeViaSqlTimestamp(event.getItem().getTimeOfStarting()),
                        convertToLocalDateTimeViaSqlTimestamp(event.getItem().getTimeOfFinishing()));
                absenceRvdRequestDc.getItem().setTotalHours(Integer.valueOf(Long.valueOf(between.toHours()).toString()));
            } else {
                absenceRvdRequestDc.getItem().setTotalHours(null);
            }

        }


    }

    protected LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        return new Timestamp(dateToConvert.getTime()).toLocalDateTime();
    }

    private void changeHint(boolean hint) {
        if (hint) {
            timeOfFinishingField.setDescription(messageBundle.getMessage("temporary_transfer_end"));
            timeOfStartingField.setDescription(messageBundle.getMessage("temporary_transfer_start"));
            timeOfFinishingField.setContextHelpText(messageBundle.getMessage("temporary_transfer_end"));
            timeOfStartingField.setContextHelpText(messageBundle.getMessage("temporary_transfer_start"));
        } else {
            timeOfFinishingField.setDescription(null);
            timeOfStartingField.setDescription(null);
            timeOfFinishingField.setContextHelpText(null);
            timeOfStartingField.setContextHelpText(null);
        }
    }

    protected void setTimesNull() {
        absenceRvdRequestDc.getItem().setTimeOfFinishing(null);
        absenceRvdRequestDc.getItem().setTimeOfStarting(null);
    }

    @Override
    protected TsadvUser getEmployee() {
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e where e.personGroup.id = :personGroupId")
                .setParameters(ParamsMap.of("personGroupId",
                        absenceRvdRequestDc.getItem().getPersonGroup().getId()))
                .view("userExt.edit").list().stream().findFirst().orElse(null);
    }

    public void exportFile(FileDescriptor fd, String columnId) {
        exportDisplay.show(fd, ExportFormat.OCTET_STREAM);
    }
}