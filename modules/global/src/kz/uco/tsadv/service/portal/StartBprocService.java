package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.NotPersisitBprocActors;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface StartBprocService {
    String NAME = "tsadv_StartBprocService";

    BpmRolesDefiner getBpmRolesDefiner(String processDefinitionKey, UUID initiatorPersonGroupId);

    List<NotPersisitBprocActors> getNotPersisitBprocActors(@Nullable TsadvUser employee, UUID initiatorPersonGroupId, BpmRolesDefiner bpmRolesDefiner);

    void saveBprocActors(UUID entityId, List<NotPersisitBprocActors> notPersisitBprocActors);
}