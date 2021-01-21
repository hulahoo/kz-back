package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.service.BprocRepositoryService;
import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessFormScreens;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.util.OperationResult;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.mixins.BprocActionMixin;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.service.AbsenceService;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeNumberService;

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
public class AbsenceRequestNewEdit extends StandardEditor<AbsenceRequest>
        implements BprocActionMixin<AbsenceRequest> {
    @Inject
    protected LookupPickerField<DicAbsenceType> typeField;
    @Inject
    protected HBoxLayout distanceWorkingConfirmBox;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<AbsenceRequest> absenceRequestDc;
    @Inject
    protected TextField<String> vacationDurationTypeField;
    @Inject
    protected CheckBox distanceWorkingConfirm;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected Form form;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private BprocRepositoryService bprocRepositoryService;
    @Inject
    private Notifications notifications;
    @Inject
    private MessageBundle messageBundle;
    @Inject
    private ProcessFormScreens processFormScreens;
    public static final String PROCESS_DEFINITION_KEY = "universalProcess";
    @Inject
    private DateField<Date> dateFromField;
    @Inject
    private AbsenceService absenceService;

    @Subscribe
    public void onInit(InitEvent event) {

        typeField.addValueChangeListener(e -> {
            boolean visible = hasStatus("APPROVING")
                    && e.getValue() != null
                    && "RJ".equals(e.getValue().getCode());
            distanceWorkingConfirmBox.setVisible(visible);

            vacationDurationTypeField.setValue(getVacationDurationType().getId());
        });

//        distanceWorkingConfirm.addValueChangeListener(e -> {
//            if (approveBtn != null) approveBtn.setEnabled(Boolean.TRUE.equals(e.getValue()));
//        }); todo approve btn
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<AbsenceRequest> event) {
        event.getEntity().setAssignmentGroup(assignmentService.getAssignmentGroup(userSession.getUser().getLogin()));
        event.getEntity().setRequestDate(new Date());
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
                    //getItem().setAbsenceDays((int) ((getItem().getDateTo().getTime() - getItem().getDateFrom().getTime()) / MILLIS_IN_DAY + 1));

                    Integer absenceDays = VacationDurationType.WORK.equals(getVacationDurationType())
                            ? absenceService.countBusinessDays(dateFrom, dateTo, absenceType, getEditedEntity().getAssignmentGroup())
                            : absenceService.countAbsenceDays(dateFrom, dateTo, absenceType, getEditedEntity().getAssignmentGroup());
                    getEditedEntity().setAbsenceDays(absenceDays);
                }
            }
        });
    }

    protected boolean hasStatus(String requestStatus) {
        return absenceRequestDc.getItem().getStatus() != null
                && requestStatus.equals(absenceRequestDc.getItem().getStatus().getCode());
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

    @Override
    public boolean beforeOpenRunProcessDialogHandler() {
        OperationResult operationResult = commitChanges();
        if (operationResult.getStatus() == OperationResult.Status.SUCCESS) {
            return true;
        }
        return false;
    }

    @Override
    public void afterCloseRunProcessDialogHandler(Screen screen, AfterCloseEvent afterCloseEvent) {
        if (Window.COMMIT_ACTION_ID.equals(((StandardCloseAction) afterCloseEvent.getCloseAction()).getActionId())) {
            closeWithCommit();
        }
    }

    @Override
    public String getProcDefinitionKey() {
        return "universalProcess";
    }
}