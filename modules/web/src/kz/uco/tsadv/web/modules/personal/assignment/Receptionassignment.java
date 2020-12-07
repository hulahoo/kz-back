package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.EmployeeConfig;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.AbsenceBalance;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.recruitment.config.CalcEndTrialPeriod;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.*;

public class Receptionassignment extends AbstractEditor<AssignmentGroupExt> {

    private static final Logger log = LoggerFactory.getLogger(Receptionassignment.class);

    protected PersonExt person;
    protected PersonGroupExt personGroup;
    protected AssignmentExt assignment;
    protected String employeeNum;
    protected String assignmentNum;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Datasource<AssignmentExt> assignmentDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Datasource<AssignmentGroupExt> assignmentGroupDs;
    @Inject
    protected Datasource<PersonExt> personDs;
    @Named("fieldGroup.hireDate")
    protected DateField<Date> hireDate;
    @Named("fieldGroup.positionName")
    protected PickerField<PositionGroupExt> positionNameField;
    @Named("fieldGroup.jobGroup")
    protected PickerField<JobGroup> jobGroup;
    @Named("fieldGroup.organizationGroup")
    protected PickerField<OrganizationGroupExt> organizationGroupField;
    @Named("fieldGroup.durationProbationPeriod")
    protected TextField<Integer> durationProbationPeriod;
    @Named("fieldGroup.unit")
    protected LookupField unit;
    @Named("fieldGroup.probationEndDate")
    protected DateField<Date> probationEndDate;
    @Named("fieldGroup.gradeGroup")
    protected PickerField<GradeGroup> gradeGroup;
    //    @Named("fieldGroup.location")
//    protected PickerField location;
    @Named("fieldGroup.calendar")
    protected PickerField<kz.uco.tsadv.modules.timesheet.model.Calendar> calendarField;
    @Named("fieldGroup.employeeNumber")
    protected TextField<String> employeeNumber;
    @Named("fieldGroup.costCenter")
    protected PickerField<DicCostCenter> costCenter;
    @Named("fieldGroup.assignmentNumber")
    protected TextField<String> assignmentNumberField;
    @Inject
    protected CommonService commonService;
    @Inject
    protected AbsenceBalanceService absenceBalanceService;
    @Inject
    protected OrderNumberService orderNumberService;
    protected boolean org, job = false;
    @Inject
    protected Configuration configuration;
    protected CalcEndTrialPeriod calcEndTrialPeriod;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected EmployeeConfig employeeConfig;
    @Inject
    protected BusinessRuleService businessRuleService;
    @Inject
    protected CommonConfig commonConfig;

    @Override
    protected void initNewItem(AssignmentGroupExt item) {
        super.initNewItem(item);
        item.setAnalytics(metadata.create(OrgAnalytics.class));
        assignment = metadata.create(AssignmentExt.class);
//        assignment.setStartDate(CommonUtils.getSystemDate());
        assignment.setEndDate(CommonUtils.getEndOfTime());
        assignment.setPrimaryFlag(true);
        ArrayList<AssignmentExt> list = new ArrayList<>();
        list.add(assignment);
        personGroup = metadata.create(PersonGroupExt.class);
        person = initPerson();
//        person.setStartDate(person.getHireDate());
        person.setEndDate(CommonUtils.getEndOfTime());
        if (person.getType() == null) {
            DicPersonType personType = commonService.getEntity(DicPersonType.class, "EMPLOYEE");
            if (personType != null) {
                person.setType(personType);
            }
        }
        employeeNum = employeeNumberService.generateEmployeeNumber(person.getType());
        assignmentNum = employeeNum + "-1";
        person.setEmployeeNumber(employeeNum);
        item.setAssignmentNumber(assignmentNum);
        personGroup.setPerson(person);
        person.setGroup(personGroup);
        personGroup.setAssignments(list);
        assignment.setPersonGroup(personGroup);
        assignment.setGroup(item);
        item.setAssignment(assignment);
        organizationGroupField.removeAction("open");
        jobGroup.removeAction("open");
        calendarField.setEditable(false);
        assignment.setDurationProbationPeriod(employeeConfig.getDurarationProbationPeriod());
        assignment.setUnit(HS_Periods.fromId(employeeConfig.getUnitProbationPeriod()));
        if (employeeConfig.getEnableOrderNumberAutogenerationForAssignments()) {
            assignment.setOrderNumber((orderNumberService.getLastAssignmentOrderNumber() + 1) + "");
        }
    }

    /**
     * Override in AA
     *
     * @return person
     */
    protected PersonExt initPerson() {
        return metadata.create(PersonExt.class);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        gradeGroup.removeAllActions();
        gradeGroup.addLookupAction();
        gradeGroup.addClearAction();

        organizationGroupField.removeAction(PickerField.LookupAction.NAME);
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(organizationGroupField) {
            public OrganizationGroupExt transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                return (OrganizationGroupExt) dataManager.reload(valueFromLookupWindow, "organizationGroupExt-receptionAssignment");
            }
        };
        organizationGroupField.addAction(lookupAction);
        lookupAction.setLookupScreen("organization-tree");
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.THIS_TAB);

        organizationGroupField.removeAction(PickerField.ClearAction.NAME);
        organizationGroupField.addClearAction();


        positionNameField.removeAction(PickerField.LookupAction.NAME);
        PickerField.LookupAction lookupAction1 = new PickerField.LookupAction(positionNameField) {
            public PositionGroupExt transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                return (PositionGroupExt) dataManager.reload(valueFromLookupWindow, "positionExt-receptionAssignment");
            }
        };

        positionNameField.addAction(lookupAction1);
        lookupAction1.setLookupScreen("base$PositionGroup.browse");
        lookupAction1.setLookupScreenOpenType(WindowManager.OpenType.THIS_TAB);

        positionNameField.removeAction(PickerField.ClearAction.NAME);
        positionNameField.addClearAction();


//        gradeGroup.removeAction(PickerField.LookupAction.NAME);
//        PickerField.LookupAction lookupAction2 = new PickerField.LookupAction(gradeGroup) {
//            public GradeGroup transformValueFromLookupWindow(Entity valueFromLookupWindow) {
//                return (GradeGroup) dataManager.reload(valueFromLookupWindow, "gradeGroup-receptionAssignment");
//            }
//        };
//
//        gradeGroup.addAction(lookupAction2);
//        lookupAction2.setLookupScreen("tsadv$Grade.browse");
//        lookupAction2.setLookupScreenOpenType(WindowManager.OpenType.THIS_TAB);
//        gradeGroup.removeAction(PickerField.ClearAction.NAME);
//        gradeGroup.addClearAction();


        employeeNumber.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().equals("")) {
                employeeNumber.setValue(employeeNum);
            }
        });
        /*positionName.addValueChangeListener(e -> {
            PositionGroupExt positionGroup
        });*/
    }

    @Override
    protected void postInit() {
        super.postInit();
        hireDate.addValueChangeListener(e -> {
            getItem().getAssignment().setAssignDate(hireDate.getValue());
            getItem();
            if (getItem().getAssignment() != null && getItem().getAssignment().getPersonGroup() != null && getItem().getAssignment().getPersonGroup().getPerson() != null) {
                getItem().getAssignment().getPersonGroup().getPerson().setStartDate((e.getValue()));
            }
        });
        organizationGroupField.addAction(new PickerField.LookupAction(organizationGroupField) {
            @Override
            public OrganizationGroupExt transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                return (OrganizationGroupExt) dataManager.reload(valueFromLookupWindow, "organizationGroupExt-view-for-requisition");
            }
        });
        positionNameField.addValueChangeListener(e -> {
            if (positionNameField.getValue() != null) {
                org = true;
                job = true;
                Map<Integer, Object> map = new HashMap<>();
                map.put(1, (positionNameField.getValue()).getId());
                if (e.getValue() == null) {
                    log.warn("PositionGroup is null in ValueChangeListener");
                }
                OrganizationGroupExt organizationGroups = e.getValue().getPosition().getOrganizationGroupExt();
                organizationGroupField.setValue(organizationGroups);
                jobGroup.setValue((positionNameField.getValue()).getPosition().getJobGroup());
                PositionExt position = positionNameField.getValue().getPosition();
                gradeGroup.setValue(position != null ? position.getGradeGroup() : null);
                getItem().getAssignment().setLocation(position != null ? position.getLocation() : null);
                getItem().getAssignment().setFte(1.0);
                checkFteAndMaxPersonsCount(position);
                if (position != null && position.getCostCenter() != null) {
                    costCenter.setValue(position.getCostCenter());
                } else if (organizationGroups.getOrganization().getCostCenter() != null) {
                    costCenter.setValue(organizationGroups.getOrganization().getCostCenter());
                } else {
                    costCenter.setValue(null);
                }
            }
        });
        organizationGroupField.addValueChangeListener(e -> {
            if ((!job) && (!org)) {
                positionNameField.setValue(null);
            }
            org = false;
            OrganizationGroupExt organizationGroupExt = organizationGroupField.getValue();
            if (organizationGroupExt != null) {
                calendarField.setEditable(true);
                if (organizationGroupExt.getAnalytics() != null) {
                    calendarField.setValue(organizationGroupExt.getAnalytics().getCalendar());
                } else {
                    calendarField.setValue(null);
                }
            } else {
                calendarField.setEditable(false);
            }
            /*if (((PositionGroupExt) positionName.getValue()).getAnalytics()!=null) {
                if (((PositionGroupExt) positionName.getValue()).getAnalytics().getCalendar()!=null) {
                    calendarField.setValue(((PositionGroupExt) positionName.getValue()).getAnalytics().getCalendar());
                }
            }*/
        });

        jobGroup.addValueChangeListener(e -> {
            if ((!job) && (!org)) {
                positionNameField.setValue(null);
            }
            job = false;

        });
        DicAssignmentStatus dicAssignmentStatus = commonService.emQuerySingleRelult(DicAssignmentStatus.class, "select e from tsadv$DicAssignmentStatus e where e.code='ACTIVE'", null);
        assignmentGroupDs.getItem().getAssignment().setAssignmentStatus(dicAssignmentStatus);

        calcEndTrialPeriod = configuration.getConfig(CalcEndTrialPeriod.class);
        durationProbationPeriod.addValueChangeListener(e -> {
            if (calcEndTrialPeriod.getCalcCountProbationPeriod()) {
                if (unit.getValue() != null && hireDate.getValue() != null) {
                    probationEndDate.setValue(getCountProbationPeriod(hireDate.getValue(), durationProbationPeriod.getValue(), ((HS_Periods) unit.getValue()).getId()));
                }
            }
        });

        unit.addValueChangeListener(e -> {
            if (calcEndTrialPeriod.getCalcCountProbationPeriod()) {
                if (durationProbationPeriod.getValue() != null && hireDate.getValue() != null) {
                    probationEndDate.setValue(getCountProbationPeriod(hireDate.getValue(), durationProbationPeriod.getValue(), ((HS_Periods) unit.getValue()).getId()));
                }
            }
        });

        hireDate.addValueChangeListener(e -> {
            if (calcEndTrialPeriod.getCalcCountProbationPeriod()) {
                if (durationProbationPeriod.getValue() != null && unit.getValue() != null && hireDate.getValue() != null) {
                    probationEndDate.setValue(getCountProbationPeriod(hireDate.getValue(), durationProbationPeriod.getValue(), ((HS_Periods) unit.getValue()).getId()));
                }
            }
            assignment.setStartDate(hireDate.getValue());
        });

    }

    @Override
    public void ready() {
        super.ready();
        employeeNumber.addValueChangeListener(e -> {
            String employeeNumber = e.getValue();
            String assignmentNumber = assignmentNumberField.getValue();
            if (!StringUtils.isBlank(employeeNumber) && !StringUtils.isBlank(assignmentNumber)) {
                String assignmentNumberSecondPart = assignmentNumber.split("-")[1];
                assignmentNumberField.setValue(employeeNumber + "-" + assignmentNumberSecondPart);
            }
        });
    }

    @Override
    protected boolean preCommit() {
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        AbsenceBalance absenceBalance = absenceBalanceService.createNewAbsenceBalance(getItem().getAssignment().getPersonGroup(), getItem().getAssignment().getPositionGroup());
        if (absenceBalance != null) {
            dataManager.commit(absenceBalance);
        }
        return super.postCommit(committed, close);
    }

    protected Date getCountProbationPeriod(Date date, int count, int units) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        if (units == 10) {
            instance.add(Calendar.DAY_OF_WEEK, count);
        } else if (units == 20) {
            instance.add(Calendar.WEEK_OF_MONTH, count);
        } else if (units == 30) {
            for (int i = 0; i < count; ) {
                i++;
                instance.add(Calendar.MONTH, 3);
            }
        } else if (units == 40) {
            instance.add(Calendar.MONTH, count);
        } else if (units == 50) {
            for (int i = 0; i < count; ) {
                i++;
                instance.add(Calendar.MONTH, 6);
            }
        } else if (units == 60) {
            instance.add(Calendar.YEAR, count);
        }
        instance.add(Calendar.DAY_OF_WEEK, -1);
        return instance.getTime();
    }


    protected void checkFteAndMaxPersonsCount(PositionExt position) {
        if (position != null) {
            String query = "SELECT count(*) " +
                    "FROM base_assignment a where ?1 BETWEEN a.start_date and a.end_date " +
                    "and a.primary_flag = TRUE " +
                    "and a.position_group_id = ?2";
            Map<Integer, Object> param = new HashMap<>();
            param.put(1, getItem().getAssignment() != null ? getItem().getAssignment().getAssignDate() : null);
            param.put(2, position.getGroup() != null ? position.getGroup().getId() : null);
            Long count = commonService.getCount(query, param);
            Double countDb = count.doubleValue();
            if (position.getFte() <= countDb && !businessRuleService.getRuleStatus("position.fte.limited").equals(RuleStatus.DISABLE)) {
                showNotification(businessRuleService.getBusinessRuleMessage("position.fte.limited"), NotificationType.TRAY);
            }
            if (position.getMaxPersons() <= countDb && !businessRuleService.getRuleStatus("position.max.persons.limited").equals(RuleStatus.DISABLE)) {
                showNotification(businessRuleService.getBusinessRuleMessage("position.max.persons.limited"), NotificationType.TRAY);
            }
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);

        if (commonConfig.getNationalIdentifierDuplicateCheckEnabled()) {
            if (assignmentService.isNationalIdentifierDuplicate(personDs.getItem().getNationalIdentifier())) {
                errors.add(getMessage("duplicateNationalIdentifierError"));
            }
        }

        if (employeeNumberService.hasEmployeeNumber(personDs.getItem().getEmployeeNumber(), personDs.getItem())) {
            errors.add("Нарушение уникальности!");
        }
    }
}