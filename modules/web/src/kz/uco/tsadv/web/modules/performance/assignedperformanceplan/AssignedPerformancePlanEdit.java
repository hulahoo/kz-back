package kz.uco.tsadv.web.modules.performance.assignedperformanceplan;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.enums.AssignedGoalTypeEnum;
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
    @Inject
    protected CollectionContainer<AssignedGoal> assignedGoalDc;
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        assignedPerformancePlanDl.load();
        assignedGoalDl.setParameter("assignedPerformancePlan", assignedPerformancePlanDc.getItem());
        assignedGoalDl.load();
    }

    @Subscribe(id = "assignedGoalDc", target = Target.DATA_CONTAINER)
    protected void onAssignedGoalDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<AssignedGoal> event) {
        String property = event.getProperty();
        if (property.equals("weight")) {
            if (event.getValue() != null && ((double) event.getValue()) > 100 || ((double) event.getValue()) < 0) {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("notBeLessOrMore")).show();
            }
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        assignedGoalDc.addItemPropertyChangeListener(assignedGoalItemPropertyChangeEvent -> {
                    if ("weight".equals(assignedGoalItemPropertyChangeEvent.getProperty())
                            || "result".equals(assignedGoalItemPropertyChangeEvent.getProperty())) {
                        double result = 0.0;
                        for (AssignedGoal item : assignedGoalDc.getItems()) {
                            result += (item.getResult() != null ? item.getResult() : 0) * item.getWeight() / 100;
                        }
                        assignedPerformancePlanDc.getItem().setResult(result);
                    }
                }
        );
    }


    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        int allWeight = 0;
        for (AssignedGoal assignedGoal : assignedGoalDc.getItems()) {
            if (assignedGoal.getWeight() > 100 || assignedGoal.getWeight() < 0) {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("notBeLessOrMore")).show();
                event.preventCommit();
            }
            allWeight += assignedGoal.getWeight();
        }
        if (allWeight > 100 || allWeight < 0) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("weightNot100")).show();
            event.preventCommit();
        }
    }


    @Subscribe("assignedGoalTable.edit")
    protected void onAssignedGoalTableEdit(Action.ActionPerformedEvent event) {
        screenBuilders.editor(assignedGoalTable)
                .withScreenId(assignedGoalTable.getSingleSelected().getGoalType().equals(AssignedGoalTypeEnum.CASCADE)
                        ? "tsadv$AssignedGoalCascade.edit"
                        : assignedGoalTable.getSingleSelected().getGoalType().equals(AssignedGoalTypeEnum.INDIVIDUAL) ?
                        "tsadv$AssignedGoalIndividual.edit"
                        : assignedGoalTable.getSingleSelected().getGoalType().equals(AssignedGoalTypeEnum.LIBRARY)
                        ? "tsadv$AssignedGoalLibrary.edit"
                        : "")
                .withOptions(new MapScreenOptions(ParamsMap.of("positionGroupId",
                        assignedPerformancePlanDc.getItem().getAssignedPerson().getCurrentAssignment() != null
                                && assignedPerformancePlanDc.getItem().getAssignedPerson()
                                .getCurrentAssignment().getPositionGroup() != null
                                ? assignedPerformancePlanDc.getItem().getAssignedPerson()
                                .getCurrentAssignment().getPositionGroup().getId()
                                : null)))
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

    @Subscribe("popup.library")
    protected void onPopupLibrary(Action.ActionPerformedEvent event) {
        screenBuilders.editor(assignedGoalTable)
                .withScreenId("tsadv$AssignedGoalLibrary.edit")
                .newEntity()
                .withInitializer(assignedGoal -> {
                    assignedGoal.setAssignedPerformancePlan(assignedPerformancePlanDc.getItem());
                    assignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                }).build().show()
                .addAfterCloseListener(afterCloseEvent ->
                        assignedGoalDl.load());
    }
}