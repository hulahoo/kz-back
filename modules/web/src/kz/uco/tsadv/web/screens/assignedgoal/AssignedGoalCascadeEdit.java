package kz.uco.tsadv.web.screens.assignedgoal;

import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

@UiController("tsadv$AssignedGoalCascade.edit")
@UiDescriptor("assigned-goal-cascade-edit.xml")
@EditedEntityContainer("assignedGoalDc")
@LoadDataBeforeShow
public class AssignedGoalCascadeEdit extends StandardEditor<AssignedGoal> {
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CollectionContainer<PersonGroupExt> personGroupDc;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (event != null) {
            try {
                MapScreenOptions options = (MapScreenOptions) event.getOptions();
                if (options.getParams().containsKey("positionGroupId")) {
                    UUID positionGroupId = (UUID) options.getParams().get("positionGroupId");
                    Map<UserExt, PersonExt> managerByPositionGroup =
                            employeeService.findManagerByPositionGroup(positionGroupId);
                    if (managerByPositionGroup != null && !managerByPositionGroup.isEmpty()) {
//                        personGroupDc.setItem(managerByPositionGroup.values().stream().findFirst().get().getGroup());
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }

}