package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;

public class PersonBrowseSiz extends AbstractLookup {
    @Inject
    private EmployeeService employeeService;

    public void redirectCard(PersonExt person, String name) {
        AssignmentExt assignment = employeeService.getAssignment(person.getGroup().getId(), "assignment.card");

        if (assignment != null) {
            openEditor("person-card", person.getGroup(), WindowManager.OpenType.THIS_TAB);
        } else {
            showNotification(getMessage("assignmentNull"));
        }
    }
}