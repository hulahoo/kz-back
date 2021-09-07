package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class PcfMainData extends EditableFrame {

    protected AssignmentExt currentAssignmanetFirst;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected FieldGroup fieldGroupIdentification;
    @Inject
    protected FieldGroup fieldGroupIdentification2;
    @Inject
    protected Label yearsCounter;


    @Named("fieldGroup.dismissalField")
    protected TextField dismissalDateField;

    @Named("fieldGroup.secondAssignmentField")
    protected CheckBox secondAssignmentField;

    @Named("fieldGroupIdentification1.dateOfBirth")
    protected Field<Date> dateOfBirth;

    protected Datasource<AssignmentExt> assignmentDs;

    public Date dismissalDate;
    public Datasource<PersonExt> personDs;

    @Inject
    private Table historyTable;
    public Datasource<PersonExt> personHistoryDs;
    @Inject
    private BusinessRuleService businessRuleService;

    @Override
    public void editable(boolean editable) {
        fieldGroup.setEditable(editable);
        fieldGroupIdentification.setEditable(editable);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        assignmentDs = getDsContext().get("assignmentDs");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dismissalDate = commonService.emQuerySingleRelult(Date.class,
                "select max(e.dismissalDate) from tsadv$Dismissal e " +
                        "join base$PersonGroupExt pg " +
                        "on pg.id = e.personGroup.id " +
                        "where e.personGroup.id = :personGroupId " +
                        "and e.deleteTs is null ",
                ParamsMap.of("personGroupId", personDs.getItem().getGroup().getId()));
        if (personDs.getItem().getHireDate() != null && dismissalDate != null) {
            if (personDs.getItem().getHireDate().before(dismissalDate)) {
                dismissalDateField.setValue(dateFormat.format(dismissalDate));
            }
        }

        if (assignmentDs != null && assignmentDs.getItem() != null) {
            if ((currentAssignmanetFirst = this.assignmentDs.getItem().getGroup().getList().get(0)) != null) {
                secondAssignmentField.setValue(assignmentService.isReHire(currentAssignmanetFirst));
            }
        }

        hireDateListener();

        if (assignmentDs == null || assignmentDs.getItem() == null) {
            Date personDateOfBirth = this.personDs.getItem().getDateOfBirth();
            yearsCounter.setValue(employeeService.calculateAge(personDateOfBirth) +
                    " " + employeeService.getYearCases(employeeService.calculateAge(personDateOfBirth)));
        } else {
            Date assignmentDateOfBirth = this.assignmentDs.getItem().getPersonGroup().getPerson().getDateOfBirth();

            yearsCounter.setValue(employeeService.calculateAge(assignmentDateOfBirth) +
                    " " + employeeService.getYearCases(employeeService.calculateAge(assignmentDateOfBirth)));
        }
        dateOfBirth.addValueChangeListener(e -> {
            if (dateOfBirth.getValue() != null) {
                yearsCounter.setValue(employeeService.calculateAge(dateOfBirth.getValue()) +
                        " " + employeeService.getYearCases(employeeService.calculateAge(dateOfBirth.getValue())));
            }
        });
    }

    @Override
    public void initDatasource() {
        personDs = getDsContext().get("personDs");
    }


    protected void hireDateListener() {
        Component hireDate = fieldGroup.getFieldNN("hireDate").getComponent();
        ((Field<Date>) hireDate).addValueChangeListener(e -> {
            if (e.getValue() != null) {
                if (checkPosition((Date) e.getValue(), assignmentDs.getItem())) {
                    showNotificationError();
                } else {
                    personDs.getItem().setStartDate(((Date) e.getValue()));
                    assignmentDs.getItem().setStartDate(((Date) e.getValue()));
                    personHistoryDs.refresh();
                }
            }
        });
    }

    protected boolean checkPosition(Date hireDate, AssignmentExt assignmentExt) {
        if (assignmentExt != null && assignmentExt.getPositionGroup() != null
                && assignmentExt.getPositionGroup().getPosition() != null) {
            Map map = new HashMap();
            map.put("id", assignmentExt.getPositionGroup().getPosition().getId());
            map.put("hireDate", hireDate);
            String query = "select e from base$PositionExt e " +
                    "       join e.positionStatus status " +
                    "       where e.id = :id" +
                    "       and :hireDate between e.startDate and e.endDate" +
                    "       and status.code = 'ACTIVE'";
            PositionExt position = commonService.getEntity(PositionExt.class, query, map, "position-view");
            if (position == null) {
                return true;
            }
        } else {
            showNotification("position is null");
        }
        return false;
    }

    protected void showNotificationError() {
        switch (businessRuleService.getRuleStatus("hireDate.check.position.active")) {
            case ERROR: {
                showNotification(businessRuleService.getBusinessRuleMessage("hireDate.check.position.active"), NotificationType.ERROR);
                break;
            }
            case WARNING: {
                showNotification(businessRuleService.getBusinessRuleMessage("hireDate.check.position.active"), NotificationType.TRAY);
                break;
            }
        }
    }

    /*protected boolean isSecondAssignment() {
        List<Dismissal> dismissals = new ArrayList<>();
        String queryString = "SELECT e from tsadv$Dismissal e " +
                "WHERE e.personGroup.id = :personGroupId " +
                "AND e.dismissalDate < :currentAssignmentStartDate";
        Map<String, Object> queryParams = new HashMap<>();
        AssignmentExt currentAssignmentFirst = assignmentDs.getItem().getGroup().getList().get(0);
        queryParams.put("personGroupId", personDs.getItem().getGroup().getId());
        queryParams.put("currentAssignmentStartDate", currentAssignmentFirst.getStartDate());
        dismissals = commonService.getEntities(Dismissal.class, queryString, queryParams, "dismissal.view");
        if (!dismissals.isEmpty()) {
            return true;
        }
        return false;
    }*/

    /*private String ageCases(int years) {
        String result = "";
        String currentLanguage = userSession.getLocale().getLanguage();
        if (currentLanguage.equals("ru") || currentLanguage.equals("kz")) {
            int iTens = years % 10;
            result = ((iTens == 1) ? "год" : ((iTens < 5 & iTens != 0) ? "года" : "лет"));
        }
        if (currentLanguage.equals("en")) {
            result="years";
        }
        return result;
    }*/

    /*private int calculateAge(Date birthDate) {
        //Date birthDate =
        Date currentDate = CommonUtils.getSystemDate();
        LocalDate birthDateLocalDate = null;
        if (birthDate != null) {
            birthDateLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
        }
        LocalDate currentDateLocalDate = new java.sql.Date(currentDate.getTime()).toLocalDate();
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDateLocalDate, currentDateLocalDate).getYears();
        } else {
            return 0;
        }
    }*/


}