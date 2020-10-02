package kz.uco.tsadv.web.persongroupext;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;

import javax.inject.Inject;

public class SubstitutedEmployeeBrowse extends AbstractLookup {
    @Inject
    protected DataManager dataManager;

    public void redirectCard(PersonGroupExt item, String columnId) {
        AssignmentExt primaryAssignment = item.getPrimaryAssignment();
        if (primaryAssignment != null) {
            primaryAssignment = dataManager.reload(primaryAssignment, "assignment.card");
            openEditor("person-card", primaryAssignment, WindowManager.OpenType.THIS_TAB);
        } else {
            showNotification("No active assignment!");
        }
    }
}