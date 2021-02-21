package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.VacationSchedule;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@UiController("tsadv$AbsenceRequestNew.edit")
@UiDescriptor("absence-request-new-edit.xml")
@EditedEntityContainer("absenceRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT)
        }
)
public class AbsenceRequestNewEdit extends AbstractBprocEditor<AbsenceRequest> {

    @Inject
    protected InstanceContainer<AbsenceRequest> absenceRequestDc;
    @Inject
    protected CollectionContainer<VacationSchedule> vacationSchedulesDc;
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
    protected TextField<String> vacationDurationTypeField;
    @Inject
    protected CheckBox distanceWorkingConfirm;
    @Inject
    protected Form form;
    @Inject
    protected DateField<Date> dateFromField;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected CollectionLoader<VacationSchedule> vacationSchedulesDl;
    @Inject
    protected InstanceLoader<AbsenceRequest> absenceRequestDl;
    @Inject
    protected LookupField<VacationSchedule> vacationScheduleField;
//    @Inject
//    protected LookupField<VacationSchedule> vacationScheduleField;

    @Subscribe
    public void onInit(InitEvent event) {
        typeField.addValueChangeListener(e -> {
            boolean visible = hasStatus("APPROVING")
                    && e.getValue() != null
                    && "RJ".equals(e.getValue().getCode());
            distanceWorkingConfirmBox.setVisible(visible);

            vacationDurationTypeField.setValue(getVacationDurationType().getId());
        });
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<AbsenceRequest> event) {
        AbsenceRequest absenceRequest = event.getEntity();
        absenceRequest.setAssignmentGroup(assignmentService.getAssignmentGroup(userSession.getUser().getLogin()));
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

        absenceRequestDc.addItemPropertyChangeListener(e -> {
            itemPropertyChangeListner(e);

        });
        vacationSchedulesDl.setQuery(vacationSchedulesDl.getQuery() + " where e.personGroup.id = :personGroupId");
        vacationSchedulesDl.setParameter("personGroupId", absenceRequestDc.getItem().getPersonGroup().getId());
        vacationSchedulesDl.load();
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
                Integer absenceDays = VacationDurationType.WORK.equals(getVacationDurationType())
                        ? absenceService.countBusinessDays(dateFrom, dateTo, absenceType, getEditedEntity().getAssignmentGroup())
                        : absenceService.countAbsenceDays(dateFrom, dateTo, absenceType, getEditedEntity().getAssignmentGroup());
                getEditedEntity().setAbsenceDays(absenceDays);
            }
        }
        if ("type".equals(e.getProperty())) {
            if (e.getItem().getType() != null && e.getItem().getPersonGroup() != null) {
                if ("ANNUAL".equals(e.getItem().getType().getCode())) {
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).id(e.getItem().getPersonGroup().getId())
                            .view("personGroup-for-absenceRequest").optional().orElse(null);
                    if (personGroupExt != null && personGroupExt.getCompany() != null
                            && personGroupExt.getCompany().getCode() != null
                            && "VCM".equals(personGroupExt.getCompany().getCode())) {
                        vacationScheduleField.setVisible(false);
                        absenceRequestDc.getItem().setVacationSchedule(null);
                    } else {
                        vacationScheduleField.setVisible(true);
                        VacationSchedule firstVacationSchedule = vacationSchedulesDc.getItems().stream().filter(
                                vacationSchedule -> vacationSchedule.getStartDate().after(CommonUtils.getSystemDate()))
                                .min((o1, o2) -> o1.getStartDate().before(o2.getStartDate()) ? 1 : -1).orElse(null);
                        vacationScheduleField.setValue(firstVacationSchedule);
                        if (firstVacationSchedule != null) {
                            absenceRequestDc.getItem().setDateFrom(firstVacationSchedule.getStartDate());
                            absenceRequestDc.getItem().setDateTo(firstVacationSchedule.getEndDate());
                        }
                    }
                } else {
                    vacationScheduleField.setVisible(false);
                    absenceRequestDc.getItem().setVacationSchedule(null);
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
}