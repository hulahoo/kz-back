package kz.uco.tsadv.web.screens.assignedgoal;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;

@UiController("tsadv$AssignedGoalIndividual.edit")
@UiDescriptor("assigned-goal-individual-edit.xml")
@EditedEntityContainer("assignedGoalDc")
@LoadDataBeforeShow
public class AssignedGoalIndividualEdit extends StandardEditor<AssignedGoal> {
}