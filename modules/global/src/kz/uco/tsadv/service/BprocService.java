package kz.uco.tsadv.service;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.uactivity.entity.ActivityType;

import java.util.List;
import java.util.UUID;

public interface BprocService {
    String NAME = "tsadv_BprocService";

    List<User> getTaskCandidates(String executionId);

    <T extends AbstractBprocRequest> void start(T entity);

    <T extends AbstractBprocRequest> void reject(T entity);

    <T extends AbstractBprocRequest> void sendNotificationToInitiator(T bprocRequest);

    <T extends AbstractBprocRequest> void sendNotificationToInitiator(T bprocRequest, String notificationTemplateCode);

    <T> T getProcessVariable(String processInstanceDataId, String variableName);

    <T extends AbstractBprocRequest> void changeRequestStatus(T entity, String code);

    <T extends AbstractBprocRequest> boolean hasActor(T entity, String taskCode);

    ProcessInstanceData getProcessInstanceData(String processInstanceBusinessKey, String processDefinitionKey);

    List<ExtTaskData> getProcessTasks(ProcessInstanceData processInstanceData);

    <T extends AbstractBprocRequest> void sendNotificationAndActivity(T entity, User user, ActivityType activityType, String notificationTemplateCode);

    List<? extends User> getActors(UUID bprocRequestId, String bprocUserTaskCode);

    void approveAbsence(AbsenceRequest absenceRequest);
}