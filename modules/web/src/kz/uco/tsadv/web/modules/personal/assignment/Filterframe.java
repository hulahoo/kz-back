
package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class Filterframe extends AbstractFrame {
    @Inject
    private OptionsGroup myTeamOptionGroupId;
    @Inject
    private LookupPickerField<Entity> organizationGroupLookupId;
    @Inject
    private Label organizationGroupIdStr;
    @Inject
    private TextField personNameId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Map<String, Boolean> myTeamOptionGroupMap = new HashMap<>();
        myTeamOptionGroupMap.put(getMessage("AssignmentMyTeamBrowse.ShowMyTeamOnly"), Boolean.TRUE);
        myTeamOptionGroupMap.put(getMessage("AssignmentMyTeamBrowse.ShowAll"), Boolean.FALSE);
        myTeamOptionGroupId.setOptionsMap(myTeamOptionGroupMap);
        myTeamOptionGroupId.setValue(Boolean.TRUE);
        organizationGroupLookupId.removeAction(organizationGroupLookupId.addOpenAction());
        organizationGroupLookupId.addValueChangeListener((e) -> {
            organizationGroupIdStr.setValue(e.getValue() != null ? ((OrganizationGroupExt) e.getValue()).getId() + "" : "");
            if (getDsContext().get("assignmentsDs")!=null){
                getDsContext().get("assignmentsDs").refresh();
            }

        });
        personNameId.addValueChangeListener((e) -> {
            if (getDsContext().get("assignmentsDs")!=null){
                getDsContext().get("assignmentsDs").refresh();
            }

        });
    }
}