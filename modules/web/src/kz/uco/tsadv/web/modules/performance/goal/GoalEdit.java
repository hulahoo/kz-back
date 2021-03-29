package kz.uco.tsadv.web.modules.performance.goal;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.Goal;

@UiController("tsadv$Goal.edit")
@UiDescriptor("goal-edit.xml")
@EditedEntityContainer("goalDc")
@DialogMode(forceDialog = true)
@LoadDataBeforeShow
public class GoalEdit extends StandardEditor<Goal> {
}