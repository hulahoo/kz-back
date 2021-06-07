package kz.uco.tsadv.web.modules.performance.assignedgoal;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.Layout;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.toolkit.ui.circliful.CirclifulServerComponent;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;

public class MyAssignedGoalBrowse extends AbstractWindow {
    @Inject
    protected Metadata metadata;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected GroupDatasource<AssignedGoal, UUID> assignedGoalsDs;

    @Inject
    protected Embedded embedded;

    @Inject
    protected Label label;

    @Inject
    protected UserSession userSession;

    @Inject
    protected Table<AssignedGoal> assignedGoalsTable;

    protected PersonExt currentPerson;
    protected boolean editing;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected LookupField lookupField;
    @Inject
    protected CollectionDatasource<PerformancePlan, UUID> performancePlansDs;
    protected PerformancePlan performancePlan;
    @Inject
    protected Label label_1;
    @Inject
    protected Label label_2;
    protected Integer result;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        currentPerson = ((PersonGroupExt) params.get("selectedPersonGroup")).getPerson();
        performancePlan = (PerformancePlan) params.get("selectedPerformacePlan");
        label.setValue("Оценка деятельности: " + performancePlan.getPerformancePlanName());
        label_1.setValue(currentPerson.getFullName());
        Utils.getPersonImageEmbedded(currentPerson, "70px", embedded);
        com.vaadin.v7.ui.Table table = assignedGoalsTable.unwrap(com.vaadin.v7.ui.Table.class);
        table.addItemClickListener(event -> {
            if (event.isDoubleClick()) {
                AssignedGoalEdit creator = (AssignedGoalEdit) openEditor("tsadv$AssignedGoal.edit", assignedGoalsDs.getItem(), WindowManager.OpenType.DIALOG, assignedGoalsDs);
                creator.addCloseListener(actionId -> {
                    assignedGoalsTable.repaint();
                    refreshLabel_2();
                });
            }
        });
    }

    protected void refreshLabel_2() {
        double commonWeight = getCommonWeight();
        label_2.setValue(getMessage(commonWeight + "%"));
        if (commonWeight > 100) {
            label_2.addStyleName("font-color-red");
            label_2.addStyleName("bold");
            label_2.removeStyleName("font-color-yellow");
            label_2.removeStyleName("font-color-green");
        } else if (commonWeight == 100) {
            label_2.addStyleName("font-color-green");
            label_2.addStyleName("bold");
            label_2.removeStyleName("font-color-yellow");
            label_2.removeStyleName("font-color-red");
        } else {
            label_2.addStyleName("font-color-yellow");
            label_2.addStyleName("bold");
            label_2.removeStyleName("font-color-red");
            label_2.removeStyleName("font-color-green");
        }
    }

    @Override
    public void ready() {
        super.ready();
        refreshLabel_2();
    }

    public Component generateColumn1(AssignedGoal entity) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        HBoxLayout priorityHBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        HBoxLayout weightHBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        Label priorityCaptionLabel = componentsFactory.createComponent(Label.class);
        Label weightCaptionLabel = componentsFactory.createComponent(Label.class);

        vBoxLayout.setSpacing(true);
        hBoxLayout.setSpacing(true);
        priorityHBoxLayout.setSpacing(true);
        weightHBoxLayout.setSpacing(true);

        priorityCaptionLabel.setValue(getMessage("AssignedGoal.myAssignedGoal.browse.priorityLabel"));
        weightCaptionLabel.setValue(getMessage("AssignedGoal.myAssignedGoal.browse.weightLabel"));

        hBoxLayout.add(priorityHBoxLayout);
        hBoxLayout.add(weightHBoxLayout);
        priorityHBoxLayout.add(priorityCaptionLabel);
        weightHBoxLayout.add(weightCaptionLabel);

        if (editing) {
            PickerField goalNameField = componentsFactory.createComponent(PickerField.class);
            LookupField priorityValueField = componentsFactory.createComponent(LookupField.class);
            TextField weightValueField = componentsFactory.createComponent(TextField.class);

            goalNameField.addLookupAction();
            goalNameField.setDatasource(assignedGoalsTable.getItemDatasource(entity), "goal");

            vBoxLayout.add(goalNameField);
            priorityHBoxLayout.add(priorityValueField);
            weightHBoxLayout.add(weightValueField);
        } else {
            Label goalNameLabel = componentsFactory.createComponent(Label.class);
            goalNameLabel.setWidth("250px");
            Label priorityValueLabel = componentsFactory.createComponent(Label.class);
            Label weightValueLabel = componentsFactory.createComponent(Label.class);

            goalNameLabel.setStyleName("ss-my-goals-name");
            priorityValueLabel.setStyleName("ss-my-goals-value");
            weightValueLabel.setStyleName("ss-my-goals-value");

            goalNameLabel.setValue(entity.getParentGoal().getGoalName());
            priorityValueLabel.setValue(entity.getPriority().getLangValue());
            weightValueLabel.setValue(entity.getWeight() + "%");

            vBoxLayout.add(goalNameLabel);
            priorityHBoxLayout.add(priorityValueLabel);
            weightHBoxLayout.add(weightValueLabel);
        }
        vBoxLayout.add(hBoxLayout);
        return vBoxLayout;
    }

    public Component generateColumn2(AssignedGoal entity) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        HBoxLayout categoryHBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        Label categoryCaptionLabel = componentsFactory.createComponent(Label.class);
        Label categoryValueLabel = componentsFactory.createComponent(Label.class);
        Label endDateLabel = componentsFactory.createComponent(Label.class);

        vBoxLayout.setSpacing(true);
        categoryHBoxLayout.setSpacing(true);

        categoryCaptionLabel.setValue(getMessage("AssignedGoal.myAssignedGoal.browse.category"));
        categoryValueLabel.setValue(entity.getParentGoal().getLibrary().getCategory().getLangValue());
        endDateLabel.setValue(getMessage("AssignedGoal.myAssignedGoal.browse.endDate") + ": " + new SimpleDateFormat("dd.MM.yyyy").format(entity.getEndDate()));

        vBoxLayout.add(categoryHBoxLayout);
        vBoxLayout.add(endDateLabel);
        categoryHBoxLayout.add(categoryCaptionLabel);
        categoryHBoxLayout.add(categoryValueLabel);

        return vBoxLayout;
    }

    public Component generateColumn3(AssignedGoal entity) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        RichTextArea textArea = componentsFactory.createComponent(RichTextArea.class);

        textArea.setValue(entity.getSuccessCriteria());
        textArea.setEditable(false);
        textArea.setHeight("100px");
        textArea.setWidth("100%");
        hBoxLayout.setWidth("100%");

        hBoxLayout.add(textArea);
        return hBoxLayout;
    }

    public Component generateColumn4(AssignedGoal entity) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        Component box = componentsFactory.createComponent(VBoxLayout.class);
        Layout vBox = (Layout) WebComponentsHelper.unwrap(box);
        CirclifulServerComponent chart = new CirclifulServerComponent();

        box.addStyleName("custom-circliful");
        box.addStyleName("custom-circliful-color");

        chart.setPercentage(entity.getTargetValue() == 0 ? 0 : Integer.valueOf(entity.getActualValue() * 100 / entity.getTargetValue()));

        vBox.addComponent(chart);
        box.setAlignment(Alignment.MIDDLE_CENTER);
        box.setWidth("150px");

        vBoxLayout.setWidth("100%");
        vBoxLayout.add(box);

        return vBoxLayout;
    }

    public Component generateColumn5(AssignedGoal entity) {
        HBoxLayout hBoxLayout4 = componentsFactory.createComponent(HBoxLayout.class);
        LinkButton deleteLinkButton = componentsFactory.createComponent(LinkButton.class);

        hBoxLayout4.setWidth("100%");
        hBoxLayout4.setHeight("100%");
        deleteLinkButton.setCaption(getMessage("table.btn.empty"));
        deleteLinkButton.setIcon("icons/close.png");
        deleteLinkButton.setAlignment(Alignment.MIDDLE_CENTER);
        deleteLinkButton.setAction(new BaseAction("") {
            @Override
            public void actionPerform(Component component) {
                assignedGoalsDs.removeItem(entity);
                refreshLabel_2();
            }
        });

        hBoxLayout4.add(deleteLinkButton);

        return hBoxLayout4;
    }

    public void onAddButtonClick() {
        AssignedGoal assignedGoal = metadata.create(AssignedGoal.class);
        assignedGoal.setAssignedByPersonGroup(((PersonExt) userSession.getAttribute("userPerson")).getGroup());
        assignedGoal.setPersonGroup(currentPerson.getGroup());
//        assignedGoal.setPerformancePlan(performancePlan);
        AssignedGoalEdit creator = (AssignedGoalEdit) openEditor("tsadv$AssignedGoal.edit", assignedGoal, WindowManager.OpenType.DIALOG, assignedGoalsDs);
        creator.addCloseListener(actionId -> {
            refreshLabel_2();
        });
    }

    public void onSaveButtonClick() {
        if (getCommonWeight() != 100 && assignedGoalsDs.getItems().size() != 0) {
            showNotification(getMessage("Attention"), getMessage("CommonWeightWarning"), NotificationType.TRAY);
        } else {
            assignedGoalsDs.getDsContext().commit();
            this.close("");
        }
    }

    public Double getCommonWeight() {
        double result = 0;
        for (AssignedGoal ag : assignedGoalsDs.getItems()) {
            result += ag.getWeight();
        }
        return result;
    }
}