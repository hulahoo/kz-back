package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.service.AbsenceBalanceService;
import kz.uco.tsadv.service.AssignmentSalaryService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.*;

public class TeamMember extends AbstractEditor<PersonExt> {

    @Inject
    protected EmployeeService employeeService;

    @Inject
    protected CommonService commonService;

    @Inject
    protected AbsenceBalanceService absenceBalanceService;

    @Inject
    protected AssignmentSalaryService assignmentSalaryService;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected Datasource<AssignmentExt> assignmentDs;

    @Inject
    protected CollectionDatasource<Salary, UUID> salaryDs;

    @Inject
    protected Datasource<PersonExt> personDs;

    @Inject
    private TextField firstName;

    @Inject
    private TextField middleName;

    @Inject
    private TextField lastName;

    @Inject
    protected TextField totalExperienceField;

    @Inject
    protected TextField positionExperienceField;

    @Inject
    protected TextField ageCounter;

    @Inject
    protected TextField vacationDaysLeftField;

    @Inject
    protected TabSheet tabSheet;

    @Inject
    protected ComponentsFactory componentsFactory;

    protected String totalExperience;

    protected String positionExperience;

    @Override
    public void ready() {
        super.ready();
        AssignmentExt assignmentExt;
        if ((assignmentExt = getCurrentAssignment()) != null) {
            assignmentDs.setItem(assignmentExt);
        }
        totalExperience = String.valueOf(employeeService.getTotalExperience(getItem().getGroup().getId(), CommonUtils.getSystemDate()));
        totalExperienceField.setValue(totalExperience);
        positionExperience = String.valueOf(employeeService.getExperienceOnCurrentPosition(getItem().getGroup()));
        positionExperienceField.setValue(positionExperience);
        ageCounter.setValue(employeeService.calculateAge(personDs.getItem().getDateOfBirth()) +
                " " + employeeService.getYearCases(employeeService.calculateAge(personDs.getItem().getDateOfBirth())));
        vacationDaysLeftField.setValue(absenceBalanceService.getCurrentAbsenceDays(personDs.getItem().getGroup()));
        if (userSessionSource.getLocale().getLanguage().equals("en")) {
            firstName.setDatasource(personDs, "firstNameLatin");
            middleName.setDatasource(personDs, "middleNameLatin");
            lastName.setDatasource(personDs, "lastNameLatin");
        }


    }

    private AssignmentExt getCurrentAssignment() {
        String queryString = "select e from base$AssignmentExt e " +
                "where e.personGroup.id = :personGroupId " +
                "and :systemDate between e.startDate and e.endDate " +
                "and e.primaryFlag = true";
        Map<String, Object> map = new HashMap<>();
        map.put("personGroupId", getItem().getGroup().getId());
        map.put("systemDate", CommonUtils.getSystemDate());
        return commonService.getEntity(AssignmentExt.class, queryString, map, "assignment.view");
    }

    public Component generateChangePercentValue(Salary element) {
        Label label = componentsFactory.createComponent(Label.class);
        List<Salary> salaries = new ArrayList(salaryDs.getItems());
        if (salaries.isEmpty()) {
            return label;
        } else if (salaryDs.getItems().size() == 1) {
            label.setValue("0");
            return label;
        } else {
            Salary previousSalary;
            if ((previousSalary = getPreviousSalary(salaries, element)) != null) {
                double changePercent = assignmentSalaryService.calculatePercentage(previousSalary.getAmount(), element.getAmount());
                if (changePercent != 0d && !Double.isNaN(changePercent)) {
                    label.setValue(String.format("%.2f", changePercent));
                } else {
                    label.setValue("0");
                }
                return label;
            } else {
                return label;
            }
        }
    }

    private Salary getPreviousSalary(List<Salary> salaries, Salary salary) {
        if (salaries == null || salaries.size() < 2 || salary == null) {
            return null;
        }
        int currentSalaryIndex = -1;
        if (salaries.contains(salary)) {
            currentSalaryIndex = salaries.indexOf(salary);
            if (currentSalaryIndex < salaries.size() - 1) {
                return salaries.get(currentSalaryIndex + 1);
            }
        }
        return null;
    }
}