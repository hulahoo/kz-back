package kz.uco.tsadv.web.screens.jobdescriptionrequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.addon.bproc.web.uicomponent.outcomespanel.OutcomesPanel;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.hr.JobDescriptionRequest;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.service.PositionService;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

@UiController("tsadv_JobDescriptionRequest.edit")
@UiDescriptor("job-description-request-edit.xml")
@EditedEntityContainer("jobDescriptionRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT),
                @Outcome(id = AbstractBprocRequest.OUTCOME_CANCEL)
        }
)
public class JobDescriptionRequestEdit extends AbstractBprocEditor<JobDescriptionRequest> {

    @Inject
    protected PositionService positionService;
    @Inject
    protected InstanceContainer<JobDescriptionRequest> jobDescriptionRequestDc;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected LinkButton reportBtn;
    @Inject
    protected TextArea<String> positionDutiesField;
    @Inject
    protected TextArea<String> generalAdditionalRequirementsField;
    @Inject
    protected TextArea<String> compulsoryQualificationRequirementsField;
    @Inject
    protected Label<String> requiredlabel;
    @Inject
    protected FileUploadField fileField;
    @Inject
    protected TextArea<String> organizationPathField;
    @Inject
    protected OrganizationService organizationService;
    protected boolean readOnly = false;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (event.getOptions() != null) {
            MapScreenOptions options = (MapScreenOptions) event.getOptions();
            Map<String, Object> params = options.getParams();
            if (params.containsKey("readOnly") && ((Boolean) params.get("readOnly"))) {
                readOnly = true;
            }
        }

    }


    @Subscribe
    protected void onAfterShowTsadv(AfterShowEvent event) {
        reportBtn.setCaption(jobDescriptionRequestDc.getItem().getPositionGroup().getFullName() + ".pdf");
        if (PersistenceHelper.isNew(jobDescriptionRequestDc.getItem())) {
            positionDutiesField.setEditable(true);
            generalAdditionalRequirementsField.setEditable(true);
            compulsoryQualificationRequirementsField.setEditable(true);
        }
        if (activeTaskData == null || activeTaskData.getName() == null
                || !"c_and_b_task2".equalsIgnoreCase(activeTaskData.getName())) {
            requiredlabel.setVisible(false);
            fileField.setVisible(false);
            fileField.setRequired(false);
        }
        StringBuilder structOrganization = new StringBuilder();
        if (jobDescriptionRequestDc.getItem() != null
                && jobDescriptionRequestDc.getItem().getPositionGroup() != null
                && jobDescriptionRequestDc.getItem().getPositionGroup().getPosition() != null
                && jobDescriptionRequestDc.getItem().getPositionGroup().getPosition().getOrganizationGroupExt() != null) {
            OrganizationGroupExt organizationGroupExt = dataManager.load(Id.of(
                    jobDescriptionRequestDc.getItem().getPositionGroup().getPosition().getOrganizationGroupExt().getId(),
                    OrganizationGroupExt.class))
                    .view("organizationGroupExt-for-struct-path").optional().orElse(null);
            List<OrganizationGroupExt> organizationList = new ArrayList<>();
            while (organizationGroupExt != null && organizationGroupExt.getOrganizationName() != null) {
                organizationList.add(organizationGroupExt);
                organizationGroupExt = organizationService.getParent(organizationGroupExt);
            }
            Collections.reverse(organizationList);
            if (organizationList.size() > 0) {
                organizationList.remove(0);
            }
            int i = 0;
            for (OrganizationGroupExt groupExt : organizationList) {
                for (int j = 0; j < organizationList.indexOf(groupExt) + 1; j++) {
                    structOrganization.append("- ");
                }
                structOrganization.append(groupExt.getOrganizationName()).append("\r\n");
            }
            organizationPathField.setValue(structOrganization.toString());
        }
        if (readOnly) {
            for (Component component : getWindow().getComponents()) {
                if (component instanceof Component.Editable) {
                    ((Component.Editable) component).setEditable(false);
                }
            }
            for (Component component : procActionButtonHBox.getComponents()) {
                component.setVisible(false);
            }
        }
    }


    @Nullable
    @Override
    protected Map<String, List<TsadvUser>> getDefaultApprovers() {
        PositionGroupExt functionalManager = positionService.getFunctionalManager(jobDescriptionRequestDc
                .getItem().getPositionGroup().getId());
        if (functionalManager == null) return null;
        List<TsadvUser> personGroups = getUsersByPersonGroups(employeeService
                .getPersonGroupByPositionGroupId(functionalManager.getId(), null));
        Map<String, List<TsadvUser>> defaultApprovers = super.getDefaultApprovers();
        if (defaultApprovers == null) {
            defaultApprovers = new HashMap<>();
        }
        if (personGroups != null && !personGroups.isEmpty()) {
            defaultApprovers.put("FUN_MANAGER", personGroups);
        }
        return defaultApprovers;
    }

    protected List<TsadvUser> getUsersByPersonGroups(List<? extends PersonGroupExt> personGroups) {
        if (CollectionUtils.isEmpty(personGroups)) return new ArrayList<>();
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e where e.personGroup in :personGroups and e.active = 'TRUE'")
                .setParameters(ParamsMap.of("personGroups", personGroups))
                .list();
    }

    @Subscribe("reportAction")
    protected void onReportAction(Action.ActionPerformedEvent event) {
        notifications.create(Notifications.NotificationType.HUMANIZED).withCaption("Здесь выдет отчет").show();
    }

    @Override
    protected BaseAction getNewOutcomeAction(OutcomesPanel outcomesPanel, Action action) {
        if ("APPROVE".equalsIgnoreCase(action.getId())) {
            return (BaseAction) action;
        }
        return super.getNewOutcomeAction(outcomesPanel, action);
    }


    @Subscribe("windowClose1")
    protected void onWindowClose1(Action.ActionPerformedEvent event) {
        closeWithDiscard();
    }
}