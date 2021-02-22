package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    String NAME = "tsadv_DocumentService";

    InsuredPerson getInsuredPerson(String type);
    List<InsuredPerson> getInsuredPersonMembers(UUID insuredPersonId);
}