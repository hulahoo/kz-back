package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.service.AbsenceService;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.util.*;

@UiController("tsadv$AbsenceRequestNew.edit")
@UiDescriptor("absence-request-new-edit.xml")
@EditedEntityContainer("absenceRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT),
                @Outcome(id = AbstractBprocRequest.OUTCOME_CANCEL)
        }
)
public class AbsenceRequestNewEdit extends AbstractBprocEditor<AbsenceRequest> {

    @Inject
    protected InstanceContainer<AbsenceRequest> absenceRequestDc;
    @Inject
    protected CollectionContainer<VacationScheduleRequest> vacationSchedulesDc;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected AbsenceService absenceService;

    @Inject
    protected LookupPickerField<DicAbsenceType> typeField;
    @Inject
    protected HBoxLayout distanceWorkingConfirmBox;
    @Inject
    protected CheckBox distanceWorkingConfirm;
    @Inject
    protected Form form;
    @Inject
    protected DateField<Date> dateFromField;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected CollectionLoader<VacationScheduleRequest> vacationSchedulesDl;
    @Inject
    protected InstanceLoader<AbsenceRequest> absenceRequestDl;
    @Inject
    protected LookupField<VacationScheduleRequest> vacationScheduleField;
    @Inject
    protected TextField<String> reasonField;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> filesDc;
    @Inject
    protected DateField<Date> scheduleStartDateField;
    @Inject
    protected DateField<Date> scheduleEndDateField;
    @Inject
    protected DateField<Date> periodDateToField;
    @Inject
    protected DateField<Date> periodDateFromField;
    @Inject
    protected CheckBox originalSheetField;
    @Inject
    protected CheckBox addNextYearField;
    @Inject
    protected DateField<Date> newStartDateField;
    @Inject
    protected DateField<Date> newEndDateField;

    @Subscribe
    public void onInit(InitEvent event) {
        typeField.addValueChangeListener(e -> {
            boolean visible = hasStatus("APPROVING")
                    && e.getValue() != null
                    && "RJ".equals(e.getValue().getCode());
            distanceWorkingConfirmBox.setVisible(visible);

            if (e.getValue() != null)
                absenceRequestDc.getItem().setVacationDurationType(
                        absenceService.getVacationDurationType(
                                userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID),
                                e.getValue().getId(),
                                getEditedEntity().getDateFrom())
                );
        });
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<AbsenceRequest> event) {
        AbsenceRequest absenceRequest = event.getEntity();
        absenceRequest.setAssignmentGroup(assignmentService.getAssignmentGroup(userSession.getUser().getLogin()));
        UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        absenceRequest.setPersonGroup(dataManager.getReference(PersonGroupExt.class, personGroupId));
    }

    @Subscribe
    public void onBeforeShow(AfterShowEvent event) {
        if (!hasStatus("DRAFT")) {
            form.setEditable(false);
        }

        if (hasStatus("DRAFT")) {
            absenceRequestDc.getItem().setRequestDate(new Date());
        }
        if (absenceRequestDc.getItem().getRequestNumber() == null) {
            absenceRequestDc.getItem().setRequestNumber(employeeNumberService.generateNextRequestNumber());
            getScreenData().getDataContext().commit();
        }

        absenceRequestDc.addItemPropertyChangeListener(this::itemPropertyChangeListner);
        vacationSchedulesDl.setQuery(vacationSchedulesDl.getQuery() + " where e.personGroup.id = :personGroupId");
        vacationSchedulesDl.setParameter("personGroupId", absenceRequestDc.getItem().getPersonGroup().getId());
        vacationSchedulesDl.load();
        if (absenceRequestDc.getItem().getType() != null &&
                absenceRequestDc.getItem().getType().getCode() != null
                && ("SICKNESS".equals(absenceRequestDc.getItem().getType().getCode())
                || "MATERNITY LEAVE".equals(absenceRequestDc.getItem().getType().getCode()))) {
            originalSheetField.setVisible(true);
            scheduleEndDateField.setVisible(true);
            scheduleStartDateField.setVisible(true);
            periodDateFromField.setVisible(true);
            periodDateToField.setVisible(true);
            addNextYearField.setVisible(true);
        } else {
            originalSheetField.setVisible(false);
            scheduleEndDateField.setVisible(false);
            scheduleStartDateField.setVisible(false);
            periodDateFromField.setVisible(false);
            periodDateToField.setVisible(false);
            addNextYearField.setVisible(false);
        }

    }

    protected void itemPropertyChangeListner(InstanceContainer.ItemPropertyChangeEvent<AbsenceRequest> e) {
        if ("dateTo".equals(e.getProperty()))
            dateFromField.setRangeEnd(getEditedEntity().getDateTo());
        if ("dateFrom".equals(e.getProperty()) || "dateTo".equals(e.getProperty()) || "type".equals(e.getProperty())) {
            Date dateFrom;
            Date dateTo;
            DicAbsenceType absenceType;
            if (((dateFrom = getEditedEntity().getDateFrom()) != null) &&
                    ((dateTo = getEditedEntity().getDateTo()) != null) &&
                    ((absenceType = getEditedEntity().getType()) != null)) {
                getEditedEntity().setAbsenceDays(
                        absenceService.countDays(
                                dateFrom,
                                dateTo,
                                absenceType.getId(),
                                userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID)));
            }
        }
        if ("type".equals(e.getProperty())) {
            if (e.getItem().getType() != null) {
                if ("ANNUAL".equals(e.getItem().getType().getCode())) {
                    if (e.getItem().getPersonGroup() != null) {
                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).id(e.getItem().getPersonGroup().getId())
                                .view("personGroup-for-absenceRequest").optional().orElse(null);
                        if (personGroupExt != null && personGroupExt.getCompany() != null
                                && personGroupExt.getCompany().getCode() != null
                                && "VCM".equals(personGroupExt.getCompany().getCode())) {
                            vacationScheduleField.setVisible(false);
                            absenceRequestDc.getItem().setVacationScheduleRequest(null);
                        } else {
                            vacationScheduleField.setVisible(true);
                            VacationScheduleRequest firstVacationSchedule = vacationSchedulesDc.getItems().stream().filter(
                                    vacationSchedule -> vacationSchedule.getStartDate().after(CommonUtils.getSystemDate()))
                                    .min(Comparator.comparing(VacationScheduleRequest::getStartDate)).orElse(null);
                            vacationScheduleField.setValue(firstVacationSchedule);
                            if (firstVacationSchedule != null) {
                                absenceRequestDc.getItem().setDateFrom(firstVacationSchedule.getStartDate());
                                absenceRequestDc.getItem().setDateTo(firstVacationSchedule.getEndDate());
                            }
                        }
                    }
                } else {
                    vacationScheduleField.setVisible(false);
                    absenceRequestDc.getItem().setVacationScheduleRequest(null);
                }
                if ("SICKNESS".equals(e.getItem().getType().getCode())
                        || "MATERNITY LEAVE".equals(e.getItem().getType().getCode())) {
                    originalSheetField.setVisible(true);
                    scheduleEndDateField.setVisible(true);
                    scheduleStartDateField.setVisible(true);
                    periodDateFromField.setVisible(true);
                    periodDateToField.setVisible(true);
                    addNextYearField.setVisible(true);
                } else {
                    originalSheetField.setVisible(false);
                    scheduleEndDateField.setVisible(false);
                    scheduleStartDateField.setVisible(false);
                    periodDateFromField.setVisible(false);
                    periodDateToField.setVisible(false);
                    addNextYearField.setVisible(false);
                }
                if ("Study leave".equals(e.getItem().getType().getCode())
                        || "MATERNITY LEAVE".equals(e.getItem().getType().getCode())
                        || "MATERNITY".equals(e.getItem().getType().getCode())
                        || "SICKNESS".equals(e.getItem().getType().getCode())
                        || "donation".equals(e.getItem().getType().getCode())) {
                    reasonField.setRequired(true);
                } else {
                    reasonField.setRequired(false);
                }
            }
        }
    }

    @Nonnull
    protected VacationDurationType getVacationDurationType() {

        VacationDurationType vacationDurationType = getVacationDurationType(absenceRequestDc.getItem().getType());
        if (vacationDurationType != null) return vacationDurationType;

        List<VacationConditions> vacationConditionsList = dataManager.load(VacationConditions.class)
                .query("select v from base$AssignmentExt a" +
                        "   join tsadv$VacationConditions v " +
                        "       on v.positionGroup = a.positionGroup " +
                        " where a.group.id = :assignmentGroupId" +
                        "   and :sysDate between a.startDate and a.endDate " +
                        "   and a.primaryFlag = 'TRUE' " +
                        "   and :sysDate between v.startDate and v.endDate " +
                        " order by v.startDate desc ")
                .parameter("assignmentGroupId",
                        absenceRequestDc.getItem().getAssignmentGroup().getId())
                .parameter("sysDate",
                        Optional.ofNullable(absenceRequestDc.getItem().getDateFrom())
                                .orElse(CommonUtils.getSystemDate()))
                .view(View.LOCAL)
                .list();

        return vacationConditionsList.stream()
                .map(VacationConditions::getVacationDurationType)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(VacationDurationType.CALENDAR);
    }

    @Nullable
    protected VacationDurationType getVacationDurationType(@Nullable DicAbsenceType absenceType) {
        return absenceType != null ? absenceType.getVacationDurationType() : null;
    }

    @Subscribe
    protected void onValidation(ValidationEvent event) {
        if (absenceRequestDc.getItem().getDateFrom() != null && absenceRequestDc.getItem().getDateTo() != null
                && absenceRequestDc.getItem().getPersonGroup() != null) {
            if (dataManager.load(Absence.class).query("select e from tsadv$Absence e " +
                    " where e.personGroup.id = :personGroupId and " +
                    " ( e.dateFrom between :startDate and :endDate or e.dateTo between :startDate and :endDate or " +
                    " (e.dateFrom > :startDate and e.dateTo < :endDate ) or (e.dateFrom < :startDate and e.dateTo > :endDate ) )")
                    .setParameters(ParamsMap.of("personGroupId",
                            absenceRequestDc.getItem().getPersonGroup().getId(),
                            "startDate", absenceRequestDc.getItem().getDateFrom(),
                            "endDate", absenceRequestDc.getItem().getDateTo())).list().size() > 0) {
                event.getErrors().add(messageBundle.getMessage("absenceAlreadyExists"));
            }
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
        if (absenceRequestDc.getItem().getFiles() == null) {
            absenceRequestDc.getItem().setFiles(new ArrayList<FileDescriptor>());
        }
        filesDc.getDisconnectedItems().add(fd);
        absenceRequestDc.getItem().getFiles().add(fd);
    }
}