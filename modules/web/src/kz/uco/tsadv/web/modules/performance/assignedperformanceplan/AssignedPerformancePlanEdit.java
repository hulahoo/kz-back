package kz.uco.tsadv.web.modules.performance.assignedperformanceplan;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.enums.AssignedGoalTypeEnum;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;

import javax.inject.Inject;


@UiController("tsadv$AssignedPerformancePlan.edit")
@UiDescriptor("assigned-performance-plan-edit.xml")
@EditedEntityContainer("assignedPerformancePlanDc")
public class AssignedPerformancePlanEdit extends StandardEditor<AssignedPerformancePlan> {

    @Inject
    protected InstanceContainer<AssignedPerformancePlan> assignedPerformancePlanDc;
    @Inject
    protected InstanceLoader<AssignedPerformancePlan> assignedPerformancePlanDl;
    @Inject
    protected CollectionLoader<AssignedGoal> assignedGoalDl;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Table<AssignedGoal> assignedGoalTable;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        assignedPerformancePlanDl.load();
        assignedGoalDl.setParameter("assignedPerformancePlan", assignedPerformancePlanDc.getItem());
        assignedGoalDl.load();
    }


    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        assignedPerformancePlanDc.setItem(getEditedEntity());
        if (PersistenceHelper.isNew(assignedPerformancePlanDc.getItem())) {
            assignedPerformancePlanDc.getItem().setStatus(CardStatusEnum.DRAFT);
        }
    }

    @Subscribe("assignedGoalTable.edit")
    protected void onAssignedGoalTableEdit(Action.ActionPerformedEvent event) {
        screenBuilders.editor(assignedGoalTable)
                .withScreenId("tsadv$AssignedGoalIndividual.edit")
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> {
                    assignedGoalDl.load();
                });
    }

    @Subscribe("popup.individual")
    protected void onPopupIndividual(Action.ActionPerformedEvent event) {
        screenBuilders.editor(assignedGoalTable)
                .withScreenId("tsadv$AssignedGoalIndividual.edit")
                .newEntity()
                .withInitializer(assignedGoal -> {
                    assignedGoal.setAssignedPerformancePlan(assignedPerformancePlanDc.getItem());
                    assignedGoal.setGoalType(AssignedGoalTypeEnum.INDIVIDUAL);
                }).build().show()
                .addAfterCloseListener(afterCloseEvent -> {
                    assignedGoalDl.load();
                });
    }

    @Subscribe("popup.cascade")
    protected void onPopupCascade(Action.ActionPerformedEvent event) {
        screenBuilders.editor(assignedGoalTable)
                .withScreenId("tsadv$AssignedGoalCascade.edit")
                .newEntity()
                .withOptions(new MapScreenOptions(ParamsMap.of("positionGroupId",
                        assignedPerformancePlanDc.getItem().getAssignedPerson().getCurrentAssignment() != null
                                && assignedPerformancePlanDc.getItem().getAssignedPerson()
                                .getCurrentAssignment().getPositionGroup() != null
                                ? assignedPerformancePlanDc.getItem().getAssignedPerson()
                                .getCurrentAssignment().getPositionGroup().getId()
                                : null)))
                .withInitializer(assignedGoal -> {
                    assignedGoal.setAssignedPerformancePlan(assignedPerformancePlanDc.getItem());
                    assignedGoal.setGoalType(AssignedGoalTypeEnum.CASCADE);
                }).build().show()
                .addAfterCloseListener(afterCloseEvent -> {
                    assignedGoalDl.load();
                });
    }

}