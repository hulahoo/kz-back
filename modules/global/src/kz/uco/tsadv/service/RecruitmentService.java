package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionSearchCandidate;
import kz.uco.tsadv.modules.recruitment.model.RequisitionSearchCandidateResult;

import java.util.List;
import java.util.UUID;

public interface RecruitmentService {
    String NAME = "tsadv_RecruitmentService";

    List<RequisitionSearchCandidateResult> getRequisitionSearchCandidate(UUID requisitionId, RequisitionSearchCandidate requisitionSearchCandidate, String language);

    Long getCurrentSequenceValue(String sequenceName);

    void checkRequisitionTimeout();

    void checkFinalCollecDate();

    void checkRequisitionStartDate();

    void checkFinalCollectDate();

    boolean getIsChangeStatus(Requisition requisition, String userLogin);
}