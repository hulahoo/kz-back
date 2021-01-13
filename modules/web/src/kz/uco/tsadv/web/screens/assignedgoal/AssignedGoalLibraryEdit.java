package kz.uco.tsadv.web.screens.assignedgoal;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.Goal;
import kz.uco.tsadv.modules.performance.model.GoalLibrary;

import javax.inject.Inject;

@UiController("tsadv$AssignedGoalLibrary.edit")
@UiDescriptor("assigned-goal-library-edit.xml")
@EditedEntityContainer("assignedGoalDc")
@LoadDataBeforeShow
public class AssignedGoalLibraryEdit extends StandardEditor<AssignedGoal> {
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionLoader<Goal> goalDl;
    @Inject
    protected InstanceContainer<AssignedGoal> assignedGoalDc;
    @Inject
    protected LookupField<Goal> parentGoalField;

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        parentGoalField.addValueChangeListener(goalValueChangeEvent -> {
            if (goalValueChangeEvent != null && goalValueChangeEvent.getValue() != null) {
                assignedGoalDc.getItem().setGoalString(goalValueChangeEvent.getValue().getGoalName());
                assignedGoalDc.getItem().setCategory(goalValueChangeEvent.getValue().getLibrary().getCategory());
            } else {
                assignedGoalDc.getItem().setGoalString(null);
            }
        });
    }


    @Subscribe("goalLibraryField")
    protected void onGoalLibraryFieldValueChange(HasValue.ValueChangeEvent<GoalLibrary> event) {
        if (event != null && event.getValue() != null) {
            goalDl.setParameter("goalList", dataManager.load(Goal.class)
                    .query("select e from tsadv$Goal e " +
                            " where e.library = :library ")
                    .parameter("library", event.getValue())
                    .list());
            goalDl.load();
        } else {
            goalDl.setParameter("goalList", null);
            goalDl.load();
        }
    }
}