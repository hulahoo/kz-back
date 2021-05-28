package kz.uco.tsadv.web.screens.assignedgoal;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.Goal;

import javax.inject.Inject;

@UiController("tsadv$AssignedGoalLibrary.edit")
@UiDescriptor("assigned-goal-library-edit.xml")
@EditedEntityContainer("assignedGoalDc")
@LoadDataBeforeShow
public class AssignedGoalLibraryEdit extends StandardEditor<AssignedGoal> {
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<AssignedGoal> assignedGoalDc;
    @Inject
    protected PickerField<Goal> parentGoalField;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        parentGoalField.addValueChangeListener(goalValueChangeEvent -> {
            if (goalValueChangeEvent != null && goalValueChangeEvent.getValue() != null) {
//                assignedGoalDc.getItem().setGoalString(goalValueChangeEvent.getValue().getGoalName());
                assignedGoalDc.getItem().setCategory(goalValueChangeEvent.getValue().getLibrary().getCategory());
            }  //                assignedGoalDc.getItem().setGoalString(null);

        });
    }

    @Subscribe("parentGoalField.lookup")
    protected void onParentGoalFieldLookup(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(parentGoalField)
                .withOptions(new MapScreenOptions(
                        ParamsMap.of("library", assignedGoalDc.getItem().getGoalLibrary())))
                .build().show();
    }
}