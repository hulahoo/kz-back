package kz.uco.tsadv.web.screens.assignedgoal;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.List;
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
    @Inject
    protected CollectionLoader<PersonGroupExt> personGroupDl;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionLoader<AssignedGoal> assignedGoalForLookupDl;
    @Inject
    protected InstanceContainer<AssignedGoal> assignedGoalDc;
    @Inject
    protected LookupField<AssignedGoal> parentAssignedGoalField;
    @Inject
    protected TextField<Double> resultField;
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (event != null) {
            try {
                MapScreenOptions options = (MapScreenOptions) event.getOptions();
                if (options.getParams().containsKey("positionGroupId")) {
                    UUID positionGroupId = (UUID) options.getParams().get("positionGroupId");
                    List<PersonGroupExt> managerListByPositionGroup =
                            employeeService.findManagerListByPositionGroup(positionGroupId, false);
                    if (managerListByPositionGroup != null && !managerListByPositionGroup.isEmpty()) {
                        personGroupDl.setParameter("personGroupList", managerListByPositionGroup);
                        personGroupDl.load();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        parentAssignedGoalField.addValueChangeListener(assignedGoalValueChangeEvent -> {
            AssignedGoal item = assignedGoalDc.getItem();
            if (assignedGoalValueChangeEvent != null && assignedGoalValueChangeEvent.getValue() != null) {
                item.setGoalString(assignedGoalValueChangeEvent.getValue().getGoalString());
                item.setWeight(assignedGoalValueChangeEvent.getValue().getWeight());
                item.setResult(assignedGoalValueChangeEvent.getValue().getResult());
                item.setSuccessCritetia(assignedGoalValueChangeEvent.getValue().getSuccessCritetia());
                item.setCategory(assignedGoalValueChangeEvent.getValue().getCategory());
            } else {
                item.setGoalString(null);
                item.setWeight(null);
                item.setResult(null);
                item.setSuccessCritetia(null);
                item.setCategory(null);
            }
        });
    }


    @Subscribe("assignedByPersonGroupField")
    protected void onAssignedByPersonGroupFieldValueChange(HasValue.ValueChangeEvent<PersonGroupExt> event) {
        AssignedGoal item = assignedGoalDc.getItem();
        if (event != null && event.getValue() != null) {
            parentAssignedGoalField.setEnabled(true);
            PersonGroupExt personGroupExt = event.getValue();
            List<AssignedGoal> assignedGoalList = getAssignedGoals(personGroupExt);
            if (!assignedGoalList.isEmpty()) {
                assignedGoalForLookupDl.setParameter("assignedGoalList", assignedGoalList);
                assignedGoalForLookupDl.load();
            } else {
                assignedGoalForLookupDl.setParameter("assignedGoalList", null);
                assignedGoalForLookupDl.load();
                item.setSuccessCritetia(null);
                item.setWeight(null);
                item.setResult(null);
                item.setParentAssignedGoal(null);
                item.setGoalString(null);
                item.setCategory(null);
            }
        } else {
            parentAssignedGoalField.setEnabled(false);
        }
    }

    private List<AssignedGoal> getAssignedGoals(PersonGroupExt personGroupExt) {
        return dataManager.load(AssignedGoal.class)
                .query("select e from tsadv$AssignedGoal e " +
                        " join e.assignedPerformancePlan app " +
                        " where app.assignedPerson = :personGroup " +
                        " and :currentDate between app.startDate and app.endDate ")
                .parameter("personGroup", personGroupExt)
                .parameter("currentDate", BaseCommonUtils.getSystemDate())
                .view("assignedGoalForKpi")
                .list();
    }
}