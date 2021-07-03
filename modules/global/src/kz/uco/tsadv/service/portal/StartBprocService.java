package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.NotPersisitBprocActors;

import java.util.List;
import java.util.UUID;

public interface StartBprocService {
    String NAME = "tsadv_StartBprocService";

    BpmRolesDefiner getBpmRolesDefiner(String processDefinitionKey, UUID employeePersonGroupId, boolean isAssistant);

    List<NotPersisitBprocActors> getNotPersisitBprocActors(UUID employeePersonGroupId,
                                                           BpmRolesDefiner bpmRolesDefiner,
                                                           boolean isAssistant);

    void saveBprocActors(UUID entityId, List<NotPersisitBprocActors> notPersisitBprocActors);
}