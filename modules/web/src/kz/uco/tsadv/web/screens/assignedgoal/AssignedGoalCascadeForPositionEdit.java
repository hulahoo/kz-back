package kz.uco.tsadv.web.screens.assignedgoal;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.Goal;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.PositionGroupGoalLink;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@UiController("tsadv$AssignedGoalCascadeForPosition.edit")
@UiDescriptor("assigned-goal-cascade-for-position-edit.xml")
@EditedEntityContainer("assignedGoalDc")
@LoadDataBeforeShow
public class AssignedGoalCascadeForPositionEdit extends StandardEditor<AssignedGoal> {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected LookupField<Goal> goal;
    @Inject
    protected InstanceContainer<AssignedGoal> assignedGoalDc;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionLoader<Goal> goalDl;
    @Inject
    protected CollectionLoader<PositionGroupExt> positionGroupDl;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (event != null) {
            try {
                MapScreenOptions options = (MapScreenOptions) event.getOptions();
                if (options.getParams().containsKey("positionGroupId")) {
                    UUID positionGroupId = (UUID) options.getParams().get("positionGroupId");
                    List<PositionGroupExt> managerListByPositionGroup =
                            employeeService.findManagerListByPositionGroupReturnListPosition(positionGroupId, false);
                    if (managerListByPositionGroup != null && !managerListByPositionGroup.isEmpty()) {
                        positionGroupDl.setParameter("positionGroupList", managerListByPositionGroup);
                        positionGroupDl.load();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        goal.addValueChangeListener(goalValueChangeEvent -> {
            AssignedGoal item = assignedGoalDc.getItem();
            if (goalValueChangeEvent != null && goalValueChangeEvent.getValue() != null) {
                item.setGoalString(goalValueChangeEvent.getValue().getGoalName());
                item.setCategory(goalValueChangeEvent.getValue().getLibrary() != null
                        ? goalValueChangeEvent.getValue().getLibrary().getCategory()
                        : null);
                item.setWeight(getWeightForPositionGoal(item.getPositionGroup()
                        , goalValueChangeEvent.getValue()));
                item.setSuccessCritetia(goalValueChangeEvent.getValue().getSuccessCriteria());
            } else {
                item.setGoalString(null);
                item.setWeight(null);
                item.setCategory(null);
                item.setSuccessCritetia(null);
            }
        });
    }

    private double getWeightForPositionGoal(PositionGroupExt positionGroup, Goal goal) {
        PositionGroupGoalLink positionGroupGoalLink = dataManager.load(PositionGroupGoalLink.class)
                .query("select e from tsadv$PositionGroupGoalLink e " +
                        " where e.positionGroup = :positionGroup " +
                        " and e.goal = :goal")
                .parameter("positionGroup", positionGroup)
                .parameter("goal", goal)
                .view("positionGroupGoalLink.edit")
                .list().stream().findFirst().orElse(null);
        if (positionGroupGoalLink != null) {
            return positionGroupGoalLink.getWeight() != null
                    ? positionGroupGoalLink.getWeight()
                    : 0.0;
        }
        return 0.0;
    }

    @Subscribe("assignedByPersonGroupField")
    protected void onAssignedByPersonGroupFieldValueChange(HasValue.ValueChangeEvent<PositionGroupExt> event) {
        AssignedGoal item = assignedGoalDc.getItem();
        if (event != null && event.getValue() != null) {
            goal.setEnabled(true);
            PositionGroupExt positionGroupExt = event.getValue();
            List<Goal> goals = getGoals(positionGroupExt);
            if (!goals.isEmpty()) {
                goalDl.setParameter("goalList", goals);
                goalDl.load();
            } else {
                goalDl.setParameter("goalList", null);
                goalDl.load();
                item.setWeight(null);
                item.setParentAssignedGoal(null);
                item.setGoalString(null);
                item.setCategory(null);
            }
        } else {
            goal.setEnabled(false);
        }
    }

    protected List<Goal> getGoals(PositionGroupExt positionGroupExt) {
        return dataManager.load(Goal.class)
                .query("select e.goal from tsadv$PositionGroupGoalLink e" +
                        " where e.positionGroup = :positionGroup")
                .parameter("positionGroup", positionGroupExt)
                .view("goal.edit")
                .list();
    }
}