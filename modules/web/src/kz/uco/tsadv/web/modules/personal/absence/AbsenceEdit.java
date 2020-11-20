package kz.uco.tsadv.web.modules.personal.absence;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebFieldGroup;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.AbsenceNotificationConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;

//Absence has listener (AssignmentExtListener)
public class AbsenceEdit extends AbstractEditor<Absence> {

    @Inject
    protected CollectionDatasource<Category, UUID> categoriesDs;
    @Inject
    protected Datasource<Absence> absenceDs;
    @Inject
    protected CollectionDatasource<DicAbsenceType, UUID> typesDs;
    @Inject
    protected CollectionDatasource<AbsenceRequest, UUID> absenceRequestsDs;
    @Inject
    protected AbsenceNotificationConfig absenceNotificationConfig;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected AbsenceBalanceService absenceBalanceService;
    @Inject
    protected DynamicAttributesService dynamicAttributesService;
    @Inject
    protected AbsenceService absenceService;
    @Inject
    protected CallStoredFunctionService callStoredFunctionService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected DatesService datesService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    @Named("fieldGroup.dateFrom")
    protected DateField<Date> dateFrom;
    @Inject
    @Named("fieldGroup.dateTo")
    protected DateField<Date> dateTo;
    @Inject
    @Named("fieldGroup.absenceDays")
    protected TextField absenceDaysField;
    @Named("fieldGroup1.file")
    protected FileUploadField fileField;
    @Named("fieldGroup1.notificationDate")
    protected DateField notificationDateField;
    @Named("fieldGroup.parentAbsence")
    protected LookupPickerField parentAbsenceField;
    @Named("fieldGroup1.absenceRequest")
    protected LookupPickerField absenceRequestField;
    @Inject
    protected RuntimePropertiesFrame runtimeProperties;
    @Named("fieldGroup.type")
    protected LookupPickerField<Entity> typeField;
    @Named("fieldGroup.additionalDays")
    protected TextField additionalDaysField;
    @Inject
    protected FieldGroup additionalFields, fieldGroup, fieldGroup1;
    //    @WindowParam(name="assignmentDs", required = true)
    @Inject
    protected TextField vacationDurationTypeLbl;

    @WindowParam(name = "assignmentDs")
    protected Datasource<AssignmentExt> assignmentDs;
    protected SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    protected FieldGroup.FieldConfig parentAbsence;
    protected Absence absence;
    protected List<AbsenceBalance> intersectingAbsenceBalances;

    protected UUID assignmentGroupId;
    protected PersonGroupExt personGroup;
    protected Integer previousAbsenceDays;
//    @Inject
//    protected TextField vacationDurationTypeLbl;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);
        dateFrom.setEditable(false);
        dateTo.setEditable(false);
        intersectingAbsenceBalances = new ArrayList<>();

        parentAbsence = fieldGroup.getFieldNN("parentAbsence");

        parentAbsence.setVisible(false);

        if (params.containsKey("offerOnlyTimecardAbsencesType")) {
            typesDs.setQuery("select e from tsadv$DicAbsenceType e where e.availableForTimecard = true");
            additionalDaysField.setVisible(false);
            notificationDateField.setVisible(false);
            fileField.setVisible(false);
            absenceRequestField.setVisible(false);
        }

        if (params.containsKey("assignmentGroupId")) {
            assignmentGroupId = (UUID) params.get("assignmentGroupId");
            List<PersonGroupExt> personGroupExtList = commonService.getEntities(PersonGroupExt.class,
                    "SELECT a.personGroup " +
                            "FROM base$AssignmentExt a " +
                            "   WHERE a.group.id = :assignmentGroupId " +
                            "      AND current_date BETWEEN a.startDate AND a.endDate",
                    ParamsMap.of("assignmentGroupId", assignmentGroupId),
                    "personGroupExt-absenceEdit");
            personGroup = personGroupExtList.isEmpty() ? null : personGroupExtList.get(0);
        }

        typeField.addValueChangeListener(e -> {
            DicAbsenceType dicAbsenceType = (DicAbsenceType) e.getValue();
            setDateFieldsEditable(dicAbsenceType);
            Category category = dynamicAttributesService.getCategory(dicAbsenceType.getCode(), "tsadv$Absence");
            categoriesDs.setItem(category);
            hideFields(dicAbsenceType.getCode());
            if (e.getValue() != null && (e.getValue().equals(e.getPrevValue()) || e.getPrevValue() == null)) return;
            absenceDs.getItem().setParentAbsence(null);
        });

        categoriesDs.addItemChangeListener(e -> {
            Category category = e.getItem();
            if (category != null) {
                WebFieldGroup webFieldGroup = (WebFieldGroup) runtimeProperties.getComponents().stream().filter(c -> c instanceof WebFieldGroup).findFirst().orElse(null);
                if (webFieldGroup != null) {
                    for (FieldGroup.FieldConfig fieldConfig : webFieldGroup.getFields()) {
                        String caption = fieldConfig.getCaption();
                        fieldConfig.setCaption(dynamicAttributesService.getLocaleTranslate(caption, category, "tsadv$Absence"));
                    }
                }
            }
        });

        dateTo.addValidator(value -> {
            if (value != null && getItem().getDateFrom() != null) {
                Date dateTo = (Date) value;
                if (dateTo.before(getItem().getDateFrom()))
                    throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg"));
            }
        });

    }

    @Override
    public void ready() {
        super.ready();
        if (!PersistenceHelper.isNew(getItem()) && getItem().getAbsenceDays() != null) {
            previousAbsenceDays = getItem().getAbsenceDays();
        }
        if (personGroup == null) {
            if (absenceDs.getItem().getPersonGroup() != null) {
                personGroup = absenceDs.getItem().getPersonGroup();
            }
        }

        dateFrom.addValueChangeListener(e -> {
            if (dateFrom.getValue() != null && dateTo.getValue() != null) {
                if (dateFrom.getValue().after(dateTo.getValue())) {
                    showNotification(getMessage("AbstractHrEditor.startDate.validatorMsg"), NotificationType.TRAY);
                }
            }
        });

        dateTo.addValueChangeListener(e -> {
            if (dateTo.getValue() != null && dateFrom.getValue() != null) {
                if (dateTo.getValue().before(dateFrom.getValue())) {
                    showNotification(getMessage("AbstractHrEditor.endDate.validatorMsg"), NotificationType.TRAY);
                }
            }
        });

        typeField.addValueChangeListener(e -> setAbsenceDays(dateFrom.getValue(), dateTo.getValue(), (DicAbsenceType) typeField.getValue()));
        dateFrom.addValueChangeListener(e -> setAbsenceDays(dateFrom.getValue(), dateTo.getValue(), (DicAbsenceType) typeField.getValue()));
        dateTo.addValueChangeListener(e -> setAbsenceDays(dateFrom.getValue(), dateTo.getValue(), (DicAbsenceType) typeField.getValue()));
    }

    protected void setAbsenceDays(Date dateFrom, Date dateTo, DicAbsenceType type) {
        if (dateFrom == null || dateTo == null || type == null) {
            absenceDaysField.setValue(0);
            return;
        }

        AssignmentExt assignmentExt = getCurrentAssignmentExt(personGroup.getId(), dateTo);
        if (assignmentExt == null) {
            showNotification(getMessage("absence.error"), getMessage("absence.assignment.is.null"), NotificationType.TRAY);
            return;
        }

        Integer absenceDays = VacationDurationType.WORK.equals(getVacationDurationType())
                ? absenceService.countBusinessDays(dateFrom, dateTo, getItem().getType(), assignmentExt.getGroup())
                : absenceService.countAbsenceDays(dateFrom, dateTo, getItem().getType(), assignmentExt.getGroup());
        absenceDaysField.setValue(absenceDays);
    }

    protected void hideFields(String name) {
        switch (name) {
            case "TRANSFER":
            case "LEAVE_EXTENTION":
            case "ANNUAL_EXTENTION":
            case "RECALL":
            case "CANCEL":
                parentAbsence.setVisible(true);
                break;
            default:
                parentAbsence.setVisible(false);
                break;
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
                        " where a.group.id = :groupId" +
                        "   and :sysDate between a.startDate and a.endDate " +
                        "   and a.primaryFlag = 'TRUE' " +
                        "   and :sysDate between v.startDate and v.endDate " +
                        " order by v.startDate desc ",
                ParamsMap.of("groupId", getAssignmentGroupId(),
                        "sysDate", Optional.ofNullable(getItem().getDateFrom()).orElse(CommonUtils.getSystemDate())),
                View.LOCAL);

        return vacationConditionsList.stream()
                .map(VacationConditions::getVacationDurationType)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(VacationDurationType.CALENDAR);
    }

    protected UUID getAssignmentGroupId() {
        return assignmentGroupId != null ? assignmentGroupId
                : assignmentDs != null && assignmentDs.getItem() != null ? assignmentDs.getItem().getGroup().getId()
                : personGroup != null ? employeeService.getAssignmentGroupByPersonGroup(personGroup).getId()
                : null;
    }

    @Nullable
    protected VacationDurationType getVacationDurationType(@Nullable DicAbsenceType absenceType) {
        return absenceType != null ? absenceType.getVacationDurationType() : null;
    }

    protected AssignmentExt getCurrentAssignmentExt(UUID personGroupId, Date dateTo) {
        Map<String, Object> map = new HashMap<>();
        map.put("absencePersonGroupId", personGroupId);
        map.put("absenceEndDate", dateTo);
        return commonService.getEntity(AssignmentExt.class, "select e" +
                        "                          from base$AssignmentExt e" +
                        "                          where e.personGroup.id = :absencePersonGroupId and e.deleteTs is null" +
                        "                          and :absenceEndDate between e.startDate and e.endDate" +
                        "                          and e.assignmentStatus.code <> 'TERMINATED'" +
                        "                          and e.primaryFlag = 'true'",
                map, "assignment.card");
    }


    @Override
    protected void initNewItem(Absence item) {
        super.initNewItem(item);
        if (item.getOrdAssignment() != null) {
            personGroup = item.getOrdAssignment().getAssignmentGroup().getAssignment().getPersonGroup();
            item.setPersonGroup(personGroup);
        } else if (personGroup != null) {
            item.setPersonGroup(personGroup);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (absenceDs.getItem().getType() != null) {
            typeField.setEditable(false);
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {

        super.postValidate(errors);
        if (absenceDs.getItem().getType() != null && absenceDs.getItem().getAbsenceDays() != null) {
            if (!hasDaysLeft() && "ANNUAL".equals(absenceDs.getItem().getType().getCode())) {
                showNotification(getMessage("ValidationErrors.notEnoughDaysOfAbsence"), NotificationType.HUMANIZED);
            }
        }
    }

    @Override
    public void commitAndClose() {
        Date dateFrom;
        Date dateTo;
        boolean isCanceling = false;
        if (getItem().getType().getCode().equals("CANCEL") || getItem().getType().getCode().equals("RECALL")) {
            isCanceling = true;
        }
        if ((dateFrom = getItem().getDateFrom()) != null && (dateTo = getItem().getDateTo()) != null && !isCanceling) {
            String language = userSessionSource.getLocale().getLanguage();
            String intersectionMessage = absenceService.intersectionExists(
                    personGroup.getId().toString(),
                    dateFrom,
                    dateTo,
                    language,
                    getItem().getId(),
                    "HR");
            if (!StringUtils.isBlank(intersectionMessage)) {
                showOptionDialog(getMessage("Attention"),
                        intersectionMessage,
                        MessageType.WARNING,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        AbsenceEdit.super.commitAndClose();
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL) {
                                    public void actionPerform(Component component) {
                                    }
                                }
                        });
            } else {
                super.commitAndClose();
            }
        } else super.commitAndClose();
    }

    public void sendAbsenceNotificationForGroup() {
        Map<String, Object> absenceGroupTemplateParams = new HashMap<>();
        if (personGroup != null) {
            absenceGroupTemplateParams.put("employeeFullNameRu", personGroup.getPersonLatinFioWithEmployeeNumber("ru"));
            absenceGroupTemplateParams.put("employeeFullNameEn", personGroup.getPersonLatinFioWithEmployeeNumber("en"));
        } else {
            absenceGroupTemplateParams.put("employeeFullNameRu", "");
            absenceGroupTemplateParams.put("employeeFullNameEn", "");
        }

        absenceGroupTemplateParams.put("absenceType", typesDs.getItem().getLangValue());
        absenceGroupTemplateParams.put("startDate", format.format(dateFrom.getValue()));
        absenceGroupTemplateParams.put("endDate", format.format(dateTo.getValue()));

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

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        absence = getItem();

        if (PersistenceHelper.isNew(getItem())) {
            if (absenceNotificationConfig.getEmployeeNotifyEnable()) {
                /*Отправить уведомление владельцу карточки*/
                Map<String, Object> map = new HashMap<>();
                UserExt userExt = employeeService.getUserExtByPersonGroupId(absence.getPersonGroup().getId());
                if (userExt != null && absence.getOrderNum() == null) {
                    notificationService.sendParametrizedNotification("absence.assets", userExt, map);
                }
            }

            /*Отправить уведомление группе*/
            if (absence.getOrderNum() == null) {
                sendAbsenceNotificationForGroup();
            }
        }

        if (committed && close) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
            callRefreshPersonBalanceSqlFunction();
        }

        return super.postCommit(committed, close);
    }

    protected boolean hasDaysLeft() {
        int daysLeftToNow = absenceBalanceService.getCurrentAbsenceDays(personGroup);
        return absenceDs.getItem().getAbsenceDays() <= daysLeftToNow;
    }

    protected PositionGroupExt getPositionGroup(UUID assignmentGroupId) {
        return employeeService.getPositionGroupByAssignmentGroupId(assignmentGroupId, null);
    }

    /* creating new ab with +1 year date */
    protected void setDateFieldsEditable(DicAbsenceType dicAbsenceType) {
        if (dicAbsenceType != null && !dicAbsenceType.toString().isEmpty()) {
            dateFrom.setEditable(true);
            dateTo.setEditable(true);
        }
    }

    protected void callRefreshPersonBalanceSqlFunction() {
        String sql = " select bal.refresh_person_balance('" + getItem().getPersonGroup().getId().toString() + "')";
        callStoredFunctionService.execCallSqlFunction(sql);
    }

}