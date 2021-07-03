package kz.uco.tsadv.service;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.personal.model.AbsenceForRecall;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.ChangeAbsenceDaysRequest;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;
import kz.uco.uactivity.entity.ActivityType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BprocService {
    String NAME = "tsadv_BprocService";

    List<? extends User> getTaskCandidates(String executionId, String viewName);

    <T extends AbstractBprocRequest> void start(T entity);

    <T extends AbstractBprocRequest> void cancel(T entity);

    <T extends AbstractBprocRequest> void reject(T entity);

    <T extends AbstractBprocRequest> void approve(T entity);

    <T extends AbstractBprocRequest> void sendNotificationAndActivityToInitiator(T bprocRequest, String notificationTemplateCode);

    <T extends AbstractBprocRequest> void sendNotificationToInitiator(T bprocRequest);

    <T extends AbstractBprocRequest> void sendNotificationToInitiator(T bprocRequest, String notificationTemplateCode);

    @Nullable
    <T> T getProcessVariable(String processInstanceDataId, String variableName);

    @Nullable
    <T> T getProcessVariable(AbstractBprocRequest entity, String variableName);

    <T extends AbstractBprocRequest> void changeRequestStatus(T entity, String code);

    <T extends AbstractBprocRequest> boolean hasActor(T entity, String taskCode);

    ProcessInstanceData getProcessInstanceData(String processInstanceBusinessKey, String processDefinitionKey);

    ExtTaskData getActiveTask(ProcessInstanceData processInstanceData);

    List<ExtTaskData> getProcessTasks(ProcessInstanceData processInstanceData);

    ProcessDefinitionData getProcessDefinitionData(String processDefinitionKey);

    FormData getStartFormData(String processDefinitionKey);

    <T extends AbstractBprocRequest> void sendNotificationAndActivity(T entity, User user, ActivityType activityType, String notificationTemplateCode);

    List<? extends User> getActors(UUID bprocRequestId, String bprocUserTaskCode, String viewName);

    void approveAbsence(AbsenceRequest absenceRequest);

    Map<String, String> getActivityIdMap(String processDefinitionKey);

    void approveAbsenceForRecall(AbsenceForRecall absenceForRecall);

    void changeStatusBprocRequest(AbstractBprocRequest entity, String status, String notificationCode);

    void changeStatusLeavingVacationRequest(LeavingVacationRequest entity, String status, String notificationCode);

    void changeStatusChangeAbsenceDaysRequest(ChangeAbsenceDaysRequest entity, String status, String notificationCode);

    void approveAssignedPerformancePlan(AssignedPerformancePlan request);

}