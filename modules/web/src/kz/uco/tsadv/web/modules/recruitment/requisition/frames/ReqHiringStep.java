package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.v7.event.DataBoundTransferable;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.Table;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.service.BusinessRuleService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author veronika.buksha
 */
public class ReqHiringStep extends AbstractFrame {

    public CollectionDatasource<RequisitionHiringStep, UUID> hiringStepsDs;
    public CollectionDatasource<RequisitionQuestionnaire, UUID> questionnairesDs;
    public Datasource<Requisition> requisitionDs;

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;

    @Inject
    protected GroupTable<RequisitionHiringStep> hiringStepsTable;
    @Named("hiringStepsTable.remove")
    protected RemoveAction hiringStepsTableRemove;
    @Inject
    protected BusinessRuleService businessRuleService;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        hiringStepsDs = (CollectionDatasource<RequisitionHiringStep, UUID>) getDsContext().get("hiringStepsDs");
        questionnairesDs = (CollectionDatasource<RequisitionQuestionnaire, UUID>) getDsContext().get("questionnairesDs");
        requisitionDs = (Datasource<Requisition>) getDsContext().get("requisitionDs");


        /*((CreateAction) hiringStepsTable.getAction("create")).setInitialValuesSupplier(() -> {
            return Collections.singletonMap("order", hiringStepsDs.getItems().stream().mapToInt(i -> i.getOrder()).max().orElseGet(() -> 0) + 1);
        });*/
        hiringStepsTableRemove.setBeforeActionPerformedHandler(() -> {
            return hiringStepsTableRemoveBeforeActionPerformedHandler();
        });
        hiringStepsTableRemove.setAfterRemoveHandler((items) -> {
            ArrayList<RequisitionHiringStep> list = new ArrayList<>(hiringStepsDs.getItems());
            for (int i = 0; i < list.size(); i++)
                list.get(i).setOrder(i + 1);

            hiringStepsDs.commit();
        });

        Table vTable = hiringStepsTable.unwrap(Table.class);
        vTable.setDragMode(Table.TableDragMode.ROW);
        vTable.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent dropEvent) {
                DataBoundTransferable transferable = (DataBoundTransferable) dropEvent.getTransferable();
                AbstractSelect.AbstractSelectTargetDetails dropData = ((AbstractSelect.AbstractSelectTargetDetails) dropEvent.getTargetDetails());

                UUID transferItemId = (UUID) transferable.getItemId();
                UUID targetItemId = (UUID) dropData.getItemIdOver();
                VerticalDropLocation dropLocation = dropData.getDropLocation();

                if (targetItemId == null) return;
                if (targetItemId.equals(transferItemId)) return;
                if (dropLocation == VerticalDropLocation.MIDDLE) return;

                RequisitionHiringStep transferHiringStep = hiringStepsDs.getItem(transferItemId);
                RequisitionHiringStep targetHiringStep = hiringStepsDs.getItem(targetItemId);

                ArrayList<RequisitionHiringStep> list = new ArrayList<>(hiringStepsDs.getItems());
                list.remove(transferHiringStep);

                switch (dropLocation) {
                    case TOP:
                        list.add(transferHiringStep.getOrder() > targetHiringStep.getOrder() ? targetHiringStep.getOrder() - 1 : targetHiringStep.getOrder() - 2, transferHiringStep);
                        break;
                    case BOTTOM:
                        list.add(transferHiringStep.getOrder() > targetHiringStep.getOrder() ? targetHiringStep.getOrder() : targetHiringStep.getOrder() - 1, transferHiringStep);
                        break;
                }

                for (int i = 0; i < list.size(); i++)
                    list.get(i).setOrder(i + 1);

                hiringStepsTable.sortBy(hiringStepsTable.getDatasource().getMetaClass().getPropertyPath("order"), true);
                hiringStepsDs.commit();
                /*log.info(targetItemId + "");
                  log.info(dropLocation() + "");*/
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new And(AbstractSelect.AcceptItem.ALL);
            }
        });

        hiringStepsTable.addGeneratedColumn("questionnaireColumn", entity -> {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            for (HiringStepQuestionnaire hiringStepQuestionnaire : entity.getHiringStep().getQuestionnaires()) {
                LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                linkButton.setCaption(hiringStepQuestionnaire.getQuestionnaire().getName());
                linkButton.setAction(new BaseAction("openQuestion") {
                    @Override
                    public void actionPerform(Component component) {
                        Map<String, Object> paramMap = new HashMap<>();
                        paramMap.put("questionnaire", hiringStepQuestionnaire.getQuestionnaire());
                        openWindow("questionnaire-detail", WindowManager.OpenType.DIALOG, paramMap);
                    }
                });
                vBoxLayout.add(linkButton);
            }
            return vBoxLayout;
        });
        hiringStepsTable.getColumn("questionnaireColumn").setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "ReqHiringStepQuestionnaireColumn"));

        hiringStepsTable.addGeneratedColumn("requiredColumn", entity -> {
            CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
            if (entity.getRequired()) {
                checkBox.setValue(Boolean.TRUE);
            } else {
                checkBox.setValue(Boolean.FALSE);
            }
            checkBox.addValueChangeListener(e -> {
                hiringStepsDs.getItem().setRequired(((Boolean) e.getValue()));
                hiringStepsDs.commit();
            });

            return checkBox;
        });
        hiringStepsTable.getColumn("requiredColumn").setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "ReqHiringStepRequiredColumn"));

    }

    protected boolean hiringStepsTableRemoveBeforeActionPerformedHandler() {
        RequisitionHiringStep singleSelected = hiringStepsTable.getSingleSelected();
        Long count = commonService.getCount(Interview.class,
                "select e from tsadv$Interview e " +
                        " where e.requisitionHiringStep.id = :hiringStepId",
                ParamsMap.of("hiringStepId", singleSelected.getId()));
        if (count > 0) {
            String message = businessRuleService.getBusinessRuleMessage("UsedRequisitionHiringStep");
            RuleStatus ruleStatus = businessRuleService.getRuleStatus("UsedRequisitionHiringStep");
            NotificationType notificationType = NotificationType.HUMANIZED;
            if (ruleStatus.getId() == 30) {
                notificationType = NotificationType.ERROR;
            } else if (ruleStatus.getId() == 20) {
                notificationType = NotificationType.WARNING;
            }
            if (!message.equals("")) {
                showNotification(message, notificationType);
            }
            return false;
        }
        return true;
    }

    public void addHiringSteps() {
        Map<String, Object> params = new HashMap<>();
        params.put(WindowParams.MULTI_SELECT.toString(), Boolean.TRUE);
        params.put("validatePreScreeningTest", Boolean.TRUE);
        List<HiringStep> hiringStepsList = new ArrayList<>();
        hiringStepsDs.getItems().forEach(hiringStep -> hiringStepsList.add(hiringStep.getHiringStep()));
        params.put("alreadyExistHiringStep", hiringStepsList);
        params.put("requisitionId", requisitionDs.getItem().getId());
        params.put("fromRequisitionEdit", new Object());
        Boolean validate = true;
        AbstractLookup abstractLookup = openLookup("tsadv$HiringStep.browse", items -> {

            for (Object item : items) {

                HiringStep hiringStep = (HiringStep) item;
                RequisitionHiringStep requisitionHiringStep = metadata.create(RequisitionHiringStep.class);
                requisitionHiringStep.setRequisition(requisitionDs.getItem());
                requisitionHiringStep.setHiringStep(hiringStep);
                requisitionHiringStep.setOrder(hiringStepsDs.getItems().stream().mapToInt(i -> i.getOrder()).max().orElseGet(() -> 0) + 1);
                requisitionHiringStep.setRequired(Boolean.TRUE);
                hiringStepsDs.addItem(requisitionHiringStep);

                for (HiringStepQuestionnaire hiringStepQuestionnaire : hiringStep.getQuestionnaires()) {
                    if (questionnairesDs.getItems().stream().noneMatch(i -> i.getQuestionnaire().getId().equals(hiringStepQuestionnaire.getQuestionnaire().getId()))) {
                        RequisitionQuestionnaire requisitionQuestionnaire = metadata.create(RequisitionQuestionnaire.class);
                        requisitionQuestionnaire.setRequisition(requisitionDs.getItem());
                        requisitionQuestionnaire.setQuestionnaire(hiringStepQuestionnaire.getQuestionnaire());
                        requisitionQuestionnaire.setWeight(10d);
                        questionnairesDs.addItem(requisitionQuestionnaire);
                    }
                }

                hiringStepsDs.commit();
                questionnairesDs.commit();
            }
        }, WindowManager.OpenType.DIALOG, params);
    }
}
