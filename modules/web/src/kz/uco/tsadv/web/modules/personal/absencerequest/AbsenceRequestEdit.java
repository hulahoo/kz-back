package kz.uco.tsadv.web.modules.personal.absencerequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bpm.entity.ProcRole;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.config.AbsenceConfig;
import kz.uco.tsadv.config.AbsenceNotificationConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.service.AbsenceService;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.bpm.editor.AbstractBpmEditor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;

public class AbsenceRequestEdit<T extends AbsenceRequest> extends AbstractBpmEditor<T> {

    public static final String PROCESS_NAME = "absenceRequest";

    @Inject
    protected Datasource<AbsenceRequest> absenceRequestDs;
    @Inject
    private CollectionDatasource<DicAbsenceType, UUID> dicAbsenceTypesDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected UserSessionSource userSessionSource;
    @Named("fieldGroup.dateFrom")
    protected DateField dateFromField;
    @Named("fieldGroup.dateTo")
    protected DateField dateToField;
    @Named("fieldGroup.type")
    protected LookupPickerField<Entity> typeField;
    @Inject
    protected CheckBox distanceWorkingConfirm;
    @Inject
    protected Label distanceWorkingConfirmLabel;
    @Inject
    protected TextField vacationDurationTypeLbl;
    @Inject
    protected HBoxLayout distanceWorkingConfirmBox;
    @Inject
    protected DatesService datesService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected AbsenceService absenceService;
    @Named("fieldGroup.absenceDays")
    private TextField absenceDaysField;
    @Inject
    private AbsenceNotificationConfig absenceNotificationConfig;
    @Inject
    protected TimeSource timeSource;

    protected Button approveBtn;
    protected SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        typeField.addValueChangeListener(e -> {
            boolean visible = isOnApproval()
                    && e.getValue() != null
                    && "RJ".equals(((DicAbsenceType) e.getValue()).getCode());
            distanceWorkingConfirmBox.setVisible(visible);

            vacationDurationTypeLbl.setValue(getVacationDurationType().getId());
        });
        distanceWorkingConfirm.addValueChangeListener(e -> {
            if (approveBtn != null) approveBtn.setEnabled(Boolean.TRUE.equals(e.getValue()));
        });
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setAssignmentGroup(getAssignmentGroupByUser());
        item.setRequestDate(timeSource.currentTimestamp());
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (isDraft()) {
            getItem().setRequestDate(timeSource.currentTimestamp());
        }
        if (getItem().getRequestNumber() == null) {
            getItem().setRequestNumber(employeeNumberService.generateNextRequestNumber());
            commit();
        }
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();

        if (!isDraft()) {
            fieldGroup.getFields().forEach(f -> f.setEditable(false));
            ((FieldGroup) getComponentNN("fieldGroup2")).getFieldNN("comment").setEditable(false);
        }

        distanceWorkingConfirm.setEditable(isOnApproval() && isSessionUserApprover && !getItem().getDistanceWorkingConfirm());
    }

    @Override
    protected void afterStartProcessListener() {
        int countDays = datesService.getFullDaysCount(getItem().getDateFrom(), getItem().getDateTo());
        if (countDays >= 30 && absenceService.isAbsenceTypeLong(getItem().getType().getId())) {
            notificationService.sendParametrizedNotification(
                    "absence.notify.longTime",
                    (UserExt) userSession.getUser(),
                    null);
        }
        if (getItem().getAbsenceDays() > 30) {
            sendAbsenceNotificationForGroup();
        }
        super.afterStartProcessListener();
    }

    public void sendAbsenceNotificationForGroup() {
        Map<String, Object> absenceGroupTemplateParams = new HashMap<>();

        if (userSession.getUser() != null) {
            PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
            personGroup = dataManager.reload(personGroup, "personGroup.person.info");
            absenceGroupTemplateParams.put("employeeFullNameRu", personGroup.getPersonLatinFioWithEmployeeNumber("ru"));
            absenceGroupTemplateParams.put("employeeFullNameEn", personGroup.getPersonLatinFioWithEmployeeNumber("en"));
        } else {
            absenceGroupTemplateParams.put("employeeFullNameRu", "");
            absenceGroupTemplateParams.put("employeeFullNameEn", "");
        }
        absenceGroupTemplateParams.put("absenceType", dicAbsenceTypesDs.getItem().getLangValue());
        absenceGroupTemplateParams.put("startDate", format.format(dateFromField.getValue()));
        absenceGroupTemplateParams.put("endDate", format.format(dateToField.getValue()));

        String absenceEmail = absenceNotificationConfig.getAbsenceEmailGroup();
        if (StringUtils.isNotBlank(absenceEmail)) {
            for (String email : absenceEmail.trim().split("\\s*(\\s|,|!|;)\\s*")) {
                String trim = email.trim();
                if (StringUtils.isNotBlank(trim))
                    notificationService.sendNotification("absence.assets.group",
                            trim,
                            absenceGroupTemplateParams,
                            absenceNotificationConfig.getAbsenceLangGroup());
            }
        }
    }

    protected void initProcActionsFrame() {
        super.initProcActionsFrame();
        //Так делать нельзя.
        BoxLayout boxLayout = (BoxLayout) getComponent("actionsBox");
        if (boxLayout != null) {
            if (!boxLayout.getOwnComponents().isEmpty()) {
                for (Component component : boxLayout.getOwnComponents()) {
                    if (component instanceof Button) {
                        Button button = (Button) component;
                        String actionId = button.getAction().getId();
                        if (actionId.equals("completeTask_approve")) {
                            Action oldAction = button.getAction();
                            ProcRole procActorRole = bpmUtils.getActiveProcTask(
                                    procActionsFrame.getProcInstance().getId(),
                                    "procTask-frame-extended"
                            ).getProcActor().getProcRole();

                            if (procActorRole.getCode().equals("HR_SPECIALIST")) {
                                String language = userSessionSource.getLocale().getLanguage();
                                String intersectionMessage = getIntersectionMessage(language, true);

                                if (!StringUtils.isBlank(intersectionMessage)) {
                                    button.setAction(new BaseAction("completeTask_approve") {
                                        @Override
                                        public void actionPerform(Component component) {
                                            showOptionDialog(getMessage("Attention"),
                                                    intersectionMessage,
                                                    MessageType.WARNING,
                                                    new Action[]{
                                                            new DialogAction(DialogAction.Type.YES, Status.PRIMARY) {
                                                                @Override
                                                                public void actionPerform(Component component) {
                                                                    oldAction.actionPerform(button);
                                                                }
                                                            },
                                                            new DialogAction(DialogAction.Type.NO, Status.NORMAL) {
                                                                public void actionPerform(Component component) {
                                                                }
                                                            }
                                                    });
                                        }
                                    });
                                }
                            }
                        } else if (actionId.equals("startProcess")) {
                            Action oldAction = button.getAction();
                            button.setAction(new BaseAction("AbsenceWarning") {
                                @Override
                                public void actionPerform(Component component) {
                                    super.actionPerform(component);
                                    if (getItem().getDateFrom() == null || getItem().getDateTo() == null) return;

                                    Integer n = getItem().getAbsenceDays();
                                    boolean isAbsenceTypeInclude = getItem().getType().getCode()
                                            .matches("MATERNITY|MILITARY|MATERNITY LEAVE|UNPAID|PAIDEDUCATION|BOLASHAK");
                                    String language = userSessionSource.getLocale().getLanguage();
                                    String intersectionMessage = getIntersectionMessage(language, false);

                                    if (!StringUtils.isBlank(intersectionMessage)) {
                                        showOptionDialog(getMessage("Attention"),
                                                intersectionMessage,
                                                MessageType.WARNING,
                                                new Action[]{
                                                        new DialogAction(DialogAction.Type.CLOSE, Status.PRIMARY) {
                                                            @Override
                                                            public void actionPerform(Component component) {
                                                            }
                                                        }
                                                });
                                    } else {
                                        Boolean popupNotificationEnable = AppBeans.get(Configuration.class)
                                                .getConfig(AbsenceConfig.class)
                                                .getPopupNotificationEnable();
                                        if (n != null && n > 30
                                                && isAbsenceTypeInclude
                                                && popupNotificationEnable) {
                                            showOptionDialog(getMessage("Attention"), getMessage("absence.request"),
                                                    MessageType.WARNING,
                                                    new Action[]{new DialogAction(DialogAction.Type.YES, Status.PRIMARY) {
                                                        @Override
                                                        public void actionPerform(Component component) {
                                                            oldAction.actionPerform(button);
                                                        }
                                                    }});
                                        } else {
                                            oldAction.actionPerform(button);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    break;
                }
            }
        }
    }

    protected String getIntersectionMessage(String language, boolean isHr) {
        PersonGroupExt personGroup = employeeService.getPersonGroupByAssignmentGroupId(getItem().getAssignmentGroup().getId());
        return absenceService.intersectionExists(
                personGroup.getId().toString(),
                getItem().getDateFrom(),
                getItem().getDateTo(),
                language,
                null,
                isHr ? "HR" : "INITIATOR");
    }

    @Override
    protected boolean beforeCompleteTaskPredicate() {
        return commit();
    }

    @Override
    public void ready() {
        super.ready();
        absenceRequestDs.addItemPropertyChangeListener(e -> {
            if ("dateFrom".equals(e.getProperty())) {
                dateToField.setRangeStart(getItem().getDateFrom());
                vacationDurationTypeLbl.setValue(getVacationDurationType().getId());
            }
            if ("dateTo".equals(e.getProperty()))
                dateFromField.setRangeEnd(getItem().getDateTo());
            if ("dateFrom".equals(e.getProperty()) || "dateTo".equals(e.getProperty()) || "type".equals(e.getProperty())) {
                Date dateFrom;
                Date dateTo;
                DicAbsenceType absenceType;
                if (((dateFrom = getItem().getDateFrom()) != null) &&
                        ((dateTo = getItem().getDateTo()) != null) &&
                        ((absenceType = getItem().getType()) != null)) {
                    //getItem().setAbsenceDays((int) ((getItem().getDateTo().getTime() - getItem().getDateFrom().getTime()) / MILLIS_IN_DAY + 1));

                    Integer absenceDays = VacationDurationType.WORK.equals(getVacationDurationType())
                            ? absenceService.countBusinessDays(dateFrom, dateTo, absenceType, getItem().getAssignmentGroup())
                            : absenceService.countAbsenceDays(dateFrom, dateTo, absenceType, getItem().getAssignmentGroup());
                    getItem().setAbsenceDays(absenceDays);
                }
            }

            if ("type".equals(e.getProperty())) {
                fieldGroup.getFieldNN("vacationDurationType")
                        .setVisible(e.getValue() != null && "ANNUAL".equals(((DicAbsenceType) e.getValue()).getCode()));
            }
        });

        vacationDurationTypeLbl.setValue(getVacationDurationType().getId());
        fieldGroup.getFieldNN("vacationDurationType")
                .setVisible(getItem().getType() != null && "ANNUAL".equals(getItem().getType().getCode()));
    }

    @Override
    protected void changeSettingsProcActionBtn(Collection<Component> vBoxComp) {
        super.changeSettingsProcActionBtn(vBoxComp);
        if (isOnApproval()
                && distanceWorkingConfirm.isVisible()
                && !Boolean.TRUE.equals(getItem().getDistanceWorkingConfirm()))
            for (Component component : vBoxComp) {
                if (component instanceof Button
                        && ((Button) component).getAction() != null
                        && "completeTask_approve".equals(((Button) component).getAction().getId())) {
                    approveBtn = (Button) component;
                    approveBtn.setEnabled(false);
                    approveBtn.setDescription(getMessage("confirm.for.approval"));
                }
            }
    }

    @Nonnull
    protected VacationDurationType getVacationDurationType() {

        VacationDurationType vacationDurationType = getVacationDurationType(getItem().getType());
        if (vacationDurationType != null) return vacationDurationType;

        List<VacationConditions> vacationConditionsList = commonService.getEntities(VacationConditions.class,
                "select v from base$AssignmentExt a" +
                        "   join tsadv$VacationConditions v " +
                        "       on v.positionGroup = a.positionGroup " +
                        " where a.group.id = :assignmentGroupId" +
                        "   and :sysDate between a.startDate and a.endDate " +
                        "   and a.primaryFlag = 'TRUE' " +
                        "   and :sysDate between v.startDate and v.endDate " +
                        " order by v.startDate desc ",
                ParamsMap.of("assignmentGroupId", getItem().getAssignmentGroup().getId(),
                        "sysDate", Optional.ofNullable(getItem().getDateFrom()).orElse(CommonUtils.getSystemDate())),
                View.LOCAL);

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

    protected AssignmentGroupExt getAssignmentGroupByUser() {
        AssignmentGroupExt assignmentGroup;
        LoadContext<AssignmentGroupExt> loadContext = LoadContext.create(AssignmentGroupExt.class)
                .setView("assignmentGroup.view")
                .setQuery(
                        LoadContext.createQuery("select e " +
                                "from base$AssignmentGroupExt e" +
                                " join e.list a " +
                                "where a.personGroup.id = :userPersonGroupId " +
                                " and :systemDate between a.startDate and a.endDate" +
                                " and a.primaryFlag = 'TRUE' " +
                                "   and a.assignmentStatus.code in ('ACTIVE', 'SUSPENDED') ")
                                .setParameter("userPersonGroupId", userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID))
                                .setParameter("systemDate", CommonUtils.getSystemDate())
                );
        assignmentGroup = dataManager.load(loadContext);

        return assignmentGroup;
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }
}