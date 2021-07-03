package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.entity.TaskData;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component(BprocAssignedPerformancePlanBean.NAME)
public class BprocAssignedPerformancePlanBean extends AbstractBprocEntityBean<AssignedPerformancePlan> {
    public static final String NAME = "tsadv_BprocAssignedPerformancePlanBean";

    @Override
    public String changeNotificationTemplateCode(String notificationTemplateCode, AssignedPerformancePlan entity) {
        if (notificationTemplateCode.equals("forChangeTemplateCode")) {
            ProcessInstanceData processInstanceData = bprocService.getProcessInstanceData(entity.getProcessInstanceBusinessKey(), entity.getProcessDefinitionKey());
            List<ExtTaskData> processTasks = bprocService.getProcessTasks(processInstanceData);
            String outcome = processTasks.stream()
                    .filter(taskData -> taskData.getEndTime() != null)
                    .max(Comparator.comparing(TaskData::getEndTime))
                    .map(ExtTaskData::getOutcome)
                    .orElse(null);
            ExtTaskData extTaskData = processTasks.stream()
                    .filter(taskData -> taskData.getEndTime() == null)
                    .findAny()
                    .orElse(null);
            String taskId = extTaskData != null ? extTaskData.getTaskDefinitionKey() : "";
            if (AbstractBprocRequest.OUTCOME_REVISION.equals(outcome)) {
                notificationTemplateCode = "bpm.kpi.initiator.revision2";
            } else if (taskId.isEmpty() || taskId.equals("line_manager_task")) {
                notificationTemplateCode = "bpm.kpi.approver";
            } else {
                notificationTemplateCode = "bpm.kpi.approver.super";
            }
        }
        return super.changeNotificationTemplateCode(notificationTemplateCode, entity);
    }

    @Override
    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return AssignedPerformancePlan.class.isAssignableFrom(tClass);
    }
}