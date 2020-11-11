package kz.uco.tsadv.web.modules.personal.persongroup;

import com.haulmont.cuba.gui.WindowManager;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;

public class PersonGroupBrowseRo extends PersonGroupBrowse {

    @Override
    public void redirectCard(PersonGroupExt personGroup, String name) {
        AssignmentExt assignment = getAssignment(personGroup.getId());

        if (assignment != null) {
            openEditor("person-card-ro", personGroup, WindowManager.OpenType.THIS_TAB);
        } else {
            showNotification("Assignment is NULL!");
        }
    }
}