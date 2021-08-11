package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.requests.OrgStructureRequest;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.WindowProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component(BprocOrgStructureRequestBean.NAME)
public class BprocOrgStructureRequestBean extends AbstractBprocEntityBean<OrgStructureRequest> {
    public static final String NAME = "tsadv_BprocOrgStructureRequestBean";
    @Inject
    protected DataManager dataManager;

    @Override
    public void approve(OrgStructureRequest entity) {
        super.approve(entity);
        sendNotificationToOther(entity, "C&B_COMPANY");
    }

    public void sendNotificationToOther(OrgStructureRequest bprocRequest, String roleCode) {
        ProcessInstanceData processInstanceData = bprocService.getProcessInstanceData(bprocRequest.getProcessInstanceBusinessKey(), bprocRequest.getProcessDefinitionKey());

        String notificationTemplateCode = bprocService.getProcessVariable(processInstanceData.getId(), "approveNotificationTemplateCode");
        if (StringUtils.isBlank(notificationTemplateCode)) return;
        ExtTaskData taskDataWithRoleCode = bprocService.getProcessTasks(processInstanceData).stream().filter(extTaskData ->
                extTaskData.getHrRole() != null
                        && extTaskData.getHrRole().getCode() != null
                        && roleCode.equalsIgnoreCase(extTaskData.getHrRole().getCode()))
                .findFirst().orElse(null);
        if (taskDataWithRoleCode != null && taskDataWithRoleCode.getAssigneeOrCandidates() != null) {
            ActivityType activityType = dataManager.load(ActivityType.class)
                    .query("select e from uactivity$ActivityType e where e.code = :code")
                    .parameter("code", "NOTIFICATION")
                    .view(new View(ActivityType.class)
                            .addProperty("code")
                            .addProperty("windowProperty",
                                    new View(WindowProperty.class).addProperty("entityName").addProperty("screenName")))
                    .one();
            for (TsadvUser assigneeOrCandidate : taskDataWithRoleCode.getAssigneeOrCandidates()) {
                bprocService.sendNotificationAndActivity(bprocRequest, dataManager.reload(assigneeOrCandidate,
                        "user-fioWithLogin"), activityType, notificationTemplateCode);
            }
        }
    }
}