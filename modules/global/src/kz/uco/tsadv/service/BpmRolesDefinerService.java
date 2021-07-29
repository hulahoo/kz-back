package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;

import java.util.UUID;

public interface BpmRolesDefinerService {
    String NAME = "tsadv_BpmRolesDefinerService";

    BpmRolesDefiner getBpmRolesDefiner(String processDefinitionKey, UUID employeePersonGroupId);
}