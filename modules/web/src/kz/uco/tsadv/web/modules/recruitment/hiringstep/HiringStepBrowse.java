package kz.uco.tsadv.web.modules.recruitment.hiringstep;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import kz.uco.tsadv.modules.recruitment.model.HiringStep;
import kz.uco.tsadv.modules.recruitment.model.HiringStepMember;
import kz.uco.tsadv.modules.recruitment.model.HiringStepQuestionnaire;
import kz.uco.tsadv.modules.recruitment.model.RequisitionHiringStep;
import kz.uco.tsadv.service.BusinessRuleService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HiringStepBrowse extends AbstractLookup {

    @Inject
    protected GroupTable<HiringStep> hiringStepsTable;
    @Named("hiringStepsTable.create")
    protected CreateAction hiringStepsTableCreate;
    @Named("hiringStepsTable.edit")
    protected EditAction hiringStepsTableEdit;
    @Inject
    protected GroupDatasource<HiringStep, UUID> hiringStepsDs;
    @Named("hiringStepsTable.remove")
    protected RemoveAction hiringStepsTableRemove;
    @Inject
    protected CommonService commonService;
    @Inject
    protected GroupBoxLayout membersGroupBox;
    @Inject
    protected Table<HiringStepMember> membersTable;
    @Inject
    private BusinessRuleService businessRuleService;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("fromRequisitionEdit")) {
            hiringStepsDs.setQuery("select e from tsadv$HiringStep e " +
                    " where current_date between e.startDate and e.endDate ");
        }
        hiringStepsTableRemove.setBeforeActionPerformedHandler(() -> {
            return hiringStepsTableRemoveBeforeActionPerformedHandler();
        });
        hiringStepsTableCreate.setAfterCommitHandler(action -> hiringStepsDs.refresh());
        hiringStepsTableEdit.setAfterCommitHandler(action -> hiringStepsDs.refresh());

        if (WindowParams.MULTI_SELECT.getBool(getContext())) {
            hiringStepsTable.setMultiSelect(true);
        }

        if (params.containsKey("validatePreScreeningTest")) {
            setLookupValidator(() -> {
                for (HiringStep hiringStep : hiringStepsTable.getSelected()) {
                    for (HiringStepQuestionnaire hr : hiringStep.getQuestionnaires()) {
                        if ("PRE_SCREEN_TEST".equals(hr.getQuestionnaire().getCategory().getCode())) {
                            if (params.containsKey("alreadyExistHiringStep")) {
                                List<HiringStep> list = ((List<HiringStep>) params.get("alreadyExistHiringStep"));
                                if (list != null) {
                                    for (HiringStep hiringStep1 : list) {
                                        for (HiringStepQuestionnaire hr1 : hiringStep1.getQuestionnaires()) {
                                            if ("PRE_SCREEN_TEST".equals(hr1.getQuestionnaire().getCategory().getCode())) {
                                                showNotification(getMessage("error"), NotificationType.ERROR);
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            });
            hiringStepsDs.setQuery("select e from tsadv$HiringStep e" +
                    " where e.id not in :param$alreadyExistHiringStep\n ");
            if (params.containsKey("fromRequisitionEdit")) {
                hiringStepsDs.setQuery("select e from tsadv$HiringStep e " +
                        " where e.id not in :param$alreadyExistHiringStep " +
                        " and current_date between e.startDate and e.endDate ");
            }
        }
    }

    protected boolean hiringStepsTableRemoveBeforeActionPerformedHandler() {
        HiringStep hiringStep = hiringStepsTable.getSingleSelected();
        Long count = commonService.getCount(RequisitionHiringStep.class,
                "select e from tsadv$RequisitionHiringStep e " +
                        " where e.hiringStep.id = :hiringStepId",
                ParamsMap.of("hiringStepId", hiringStep.getId()));
        if (count > 0) {
            String message = businessRuleService.getBusinessRuleMessage("UsedHiringStep");
            RuleStatus ruleStatus = businessRuleService.getRuleStatus("UsedHiringStep");
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
}