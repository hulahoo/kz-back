package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface DocumentService {
    String NAME = "tsadv_DocumentService";

    List<InsuredPerson> getMyInsuraces();
    InsuredPerson getInsuredPerson(String type);
    List<InsuredPerson> getInsuredPersonMembers(UUID insuredPersonId);
    Boolean checkPersonInsure(UUID personGroupId,UUID contractId);

    BigDecimal calcAmount(UUID insuranceContractId,
                          UUID personGroupExtId,
                          Date bith,
                          UUID relativeTypeId);
}