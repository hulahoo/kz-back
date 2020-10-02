package kz.uco.tsadv.web.screens;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.service.AssignmentSalaryService;

import javax.inject.Inject;
import java.util.*;

public class MyDetails extends AbstractWindow {

    @Inject
    private AssignmentSalaryService assignmentSalaryService;
    @Inject
    private CollectionDatasource<Salary, UUID> salaryDs;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CommonService commonService;
    @Inject
    private Datasource<AssignmentExt> assignmentDs;
    @Inject
    private Datasource<PersonExt> personDs;
    @Inject
    private UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personDs.setItem(getCurrentPerson());
        assignmentDs.setItem(getCurrentAssignment());
    }


    private PersonExt getCurrentPerson() {
        UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (personGroupId == null) {
            throw new ItemNotFoundException("noPerson");
        }

        return commonService.getEntity(PersonExt.class,
                " SELECT e from base$PersonExt e " +
                        "  WHERE e.group.id = :personGroupId " +
                        "  AND :systemDate between e.startDate and e.endDate",
                ParamsMap.of("personGroupId", personGroupId,
                        "systemDate", CommonUtils.getSystemDate()),
                "person.teamMember");
    }

    private AssignmentExt getCurrentAssignment() {
        UUID assignmentGroupId = userSession.getAttribute(StaticVariable.ASSIGNMENT_GROUP_ID);
        if (assignmentGroupId == null) {
            return null;
        }
        List<AssignmentExt> list = commonService.getEntities(AssignmentExt.class,
                "select e from base$AssignmentExt e " +
                        "where e.group.id = :groupId " +
                        "and :systemDate between e.startDate and e.endDate " +
                        "and e.primaryFlag = true"
                , ParamsMap.of("groupId", assignmentGroupId
                        , "systemDate", CommonUtils.getSystemDate()), "assignment.view");
        return list.isEmpty() ? null : list.get(0);
    }

    public Component generateChangePercentValue(Salary element) {
        Label label = componentsFactory.createComponent(Label.class);
        Collection<Salary> salaries = salaryDs.getItems();
        Salary previousSalary = getPreviousSalary(salaries, element);
        if (previousSalary != null) {
            double changePercent = assignmentSalaryService.calculatePercentage(previousSalary.getAmount(), element.getAmount());
            label.setValue(changePercent != 0d && !Double.isNaN(changePercent) ? String.format("%.2f", changePercent) : "0");
        }
        return label;
    }

    private Salary getPreviousSalary(Collection<Salary> salaries, Salary salary) {
        if (salaries == null || salary == null) {
            return null;
        }

        for (Iterator iterator = salaries.iterator(); iterator.hasNext(); ) {
            if (((Salary) iterator.next()).getId().equals(salary.getId())) {
                return iterator.hasNext() ? (Salary) iterator.next() : null;
            }
        }
        return null;
    }

    public void close() {
        close(CLOSE_ACTION_ID);
    }
}