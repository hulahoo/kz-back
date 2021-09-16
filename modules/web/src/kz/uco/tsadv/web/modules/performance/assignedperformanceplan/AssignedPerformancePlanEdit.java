package kz.uco.tsadv.web.modules.performance.assignedperformanceplan;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.modules.learning.model.AttestationParticipant;
import kz.uco.tsadv.modules.performance.dictionary.DicPerformanceStage;
import kz.uco.tsadv.modules.performance.enums.AssignedGoalTypeEnum;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.performance.model.Goal;
import kz.uco.tsadv.web.screens.assignedperformanceplan.ModalAssignedPerformancePlanEdit;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlanHistory;

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
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ExtAppPropertiesConfig extAppPropertiesConfig;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CollectionLoader<AssignedPerformancePlanHistory> assignedHistoryDl;
    @Inject
    protected CollectionLoader<DicPerformanceStage> dicPerformanceStageDl;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        assignedPerformancePlanDl.load();
        assignedGoalDl.setParameter("assignedPerformancePlan", assignedPerformancePlanDc.getItem());
        assignedGoalDl.load();
        assignedHistoryDl.setParameter("assignedPerformancePlan", assignedPerformancePlanDc.getItem());
        assignedHistoryDl.load();
        dicPerformanceStageDl.load();
    }

    @Subscribe(id = "assignedGoalDc", target = Target.DATA_CONTAINER)
    protected void onAssignedGoalDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<AssignedGoal> event) {
        String property = event.getProperty();
        if (property.equals("weight")) {
            if (event.getValue() != null && (((double) event.getValue()) > 100 || ((double) event.getValue()) < 0)) {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("notBeLessOrMore")).show();
            }
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        assignedGoalDc.addItemPropertyChangeListener(assignedGoalItemPropertyChangeEvent -> {
                    if ("weight".equals(assignedGoalItemPropertyChangeEvent.getProperty())
                            || "result".equals(assignedGoalItemPropertyChangeEvent.getProperty())
                            || "goalString".equals(assignedGoalItemPropertyChangeEvent.getProperty())) {
                        double result = 0.0;
                        for (AssignedGoal item : assignedGoalDc.getItems()) {
                            result += (item.getResult() != null ? item.getResult() : 0)
                                    * (item.getWeight() != null
                                    ? item.getWeight()
                                    : 0.0)
                                    / 100;
                        }
                        assignedPerformancePlanDc.getItem().setResult(result);
                        dataManager.commit(assignedPerformancePlanDc.getItem());
                        assignedPerformancePlanDl.load();
//                        getScreenData().getDataContext().merge(assignedGoalDc.getItem());
//                        getScreenData().getDataContext().commit();
                    }
                }
        );
        assignedGoalDc.addCollectionChangeListener(assignedGoalCollectionChangeEvent -> {
            if (assignedGoalCollectionChangeEvent != null
                    && (assignedGoalCollectionChangeEvent.getChangeType().equals(CollectionChangeType.ADD_ITEMS)
                    || assignedGoalCollectionChangeEvent.getChangeType().equals(CollectionChangeType.REMOVE_ITEMS))) {
                double result = 0.0;
                for (AssignedGoal item : assignedGoalDc.getItems()) {
                    result += (item.getResult() != null ? item.getResult() : 0)
                            * (item.getWeight() != null
                            ? item.getWeight()
                            : 0.0)
                            / 100;
                }
                assignedPerformancePlanDc.getItem().setResult(result);
                dataManager.commit(assignedPerformancePlanDc.getItem());
                assignedPerformancePlanDl.load();
            }
        });
        assignedPerformancePlanDc.addItemPropertyChangeListener(assignedPerformancePlanItemPropertyChangeEvent -> {
            if ("extraPoint".equals(assignedPerformancePlanItemPropertyChangeEvent.getProperty())) {
                assignedPerformancePlanDc.getItem().setFinalScore(
                        ((double) (assignedPerformancePlanItemPropertyChangeEvent.getValue() != null
                                ? assignedPerformancePlanItemPropertyChangeEvent.getValue()
                                : 0.0))
                                + assignedPerformancePlanDc.getItem().getKpiScore());
            }
        });
    }

    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        int allWeight = 0;
        for (AssignedGoal assignedGoal : assignedGoalDc.getItems()) {
            if (assignedGoal.getWeight() != null) {
                if (assignedGoal.getWeight() > 100 || assignedGoal.getWeight() < 0) {
                    notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                            .withCaption(messageBundle.getMessage("notBeLessOrMore")).show();
                    event.preventCommit();
                }
            } else {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("fillWeight")).show();
                event.preventCommit();
            }
            allWeight += assignedGoal.getWeight() != null ? assignedGoal.getWeight() : 0.0;
        }
        if (allWeight > 100 || allWeight < 0) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("weightNot100")).show();
            event.preventCommit();
        }
    }


    @Subscribe("assignedGoalTable.edit")
    protected void onAssignedGoalTableEdit(Action.ActionPerformedEvent event) {
        getScreenData().getDataContext().commit();
        screenBuilders.editor(assignedGoalTable)
                .withScreenId(assignedGoalTable.getSingleSelected().getGoalType().equals(AssignedGoalTypeEnum.INDIVIDUAL)
                        ? "tsadv$AssignedGoalIndividual.edit"
                        : assignedGoalTable.getSingleSelected().getGoalType().equals(AssignedGoalTypeEnum.LIBRARY)
                        ? "tsadv$AssignedGoalLibrary.edit"
                        : assignedGoalTable.getSingleSelected().getGoalType().equals(AssignedGoalTypeEnum.CASCADE)
                        && extAppPropertiesConfig.getCascadeInPerson()
                        ? "tsadv$AssignedGoalCascade.edit"
                        : "tsadv$AssignedGoalCascadeForPosition.edit")
                .withOptions(new MapScreenOptions(ParamsMap.of("positionGroupId",
                        assignedPerformancePlanDc.getItem().getAssignedPerson().getCurrentAssignment() != null
                                && assignedPerformancePlanDc.getItem().getAssignedPerson()
                                .getCurrentAssignment().getPositionGroup() != null
                                ? assignedPerformancePlanDc.getItem().getAssignedPerson()
                                .getCurrentAssignment().getPositionGroup().getId()
                                : null)))
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> assignedGoalDl.load());
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
                .addAfterCloseListener(afterCloseEvent -> assignedGoalDl.load());
    }

    @Subscribe("popup.cascade")
    protected void onPopupCascade(Action.ActionPerformedEvent event) {
        screenBuilders.editor(assignedGoalTable)
                .withScreenId(extAppPropertiesConfig.getCascadeInPerson()
                        ? "tsadv$AssignedGoalCascade.edit"
                        : "tsadv$AssignedGoalCascadeForPosition.edit")
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
                .addAfterCloseListener(afterCloseEvent -> assignedGoalDl.load());
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

    @Subscribe("extraPointBtn")
    protected void onExtraPointBtnClick(Button.ClickEvent event) {
        screenBuilders.editor(AssignedPerformancePlan.class, this)
                .editEntity(assignedPerformancePlanDc.getItem())
                .withScreenClass(ModalAssignedPerformancePlanEdit.class)
                .build().show();
    }

    public Component getGoalName(AssignedGoal entity) {
        Label label = componentsFactory.createComponent(Label.class);
        if (entity.getGoalString() != null && !entity.getGoalString().isEmpty()) {
            label.setValue(entity.getGoalString());
        } else {
            Goal goal = dataManager.load(Goal.class)
                    .query("select e from tsadv$Goal e " +
                            " where e = :goal ")
                    .parameter("goal", entity.getGoal())
                    .list().stream().findFirst().orElse(null);
            label.setValue(goal != null ? goal.getGoalLang() : "");
        }
        return label;
    }
}