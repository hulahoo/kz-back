package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
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
        });
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
}