package kz.uco.tsadv.service;

import java.util.List;

//todo remove
public interface BprocUtilService {
    String NAME = "tsadv_BprocUtilService";

    List<String> getAssignee(String processDefinitionKey, String entityId, String approverCode);

    boolean hasAssignee(String processDefinitionKey, String entityId, String approverCode);

    void setStatus(String statusCode, String entityName, String entityId);

    void sendNotification(String assigneeUserId, String notificationCode, String entityName, String entityId);

    void doneActivity(String assigneeUserId, String entityId);

    void taskCreate(String executionId);

    void taskComplete(String executionId);

    void taskDelete(String executionId);

    void executionStart(String executionId);

    void executionEnd(String executionId);

    void executionTake(String executionId);

    void reject();

    void approve(String entityName, String entityId);

}