package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.modules.personal.model.ScheduleOffsetsRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface DocumentService {
    String NAME = "tsadv_DocumentService";

    List<InsuredPerson> getMyInsuraces();

    InsuredPerson getInsuredPerson(String type);

    List<InsuredPerson> getInsuredPersonMembers(UUID insuredPersonId);

    List<InsuredPerson> getInsuredPersonMembersWithNewContract(UUID insuredPersonId, UUID contractId);

    Boolean checkPersonInsure(UUID personGroupId, UUID contractId);

    BigDecimal calcAmount(UUID insuranceContractId,
                          UUID personGroupExtId,
                          Date bith,
                          UUID relativeTypeId);

    List<ScheduleOffsetsRequest> getOffsetRequestsByPgId(UUID personGroupExtId);

    ScheduleOffsetsRequest getOffsetRequestsNew();

    BigDecimal calcTotalAmount(UUID insuredPersonId);

    InsuredPerson commitFromPortal(InsuredPerson insuredPerson);
}