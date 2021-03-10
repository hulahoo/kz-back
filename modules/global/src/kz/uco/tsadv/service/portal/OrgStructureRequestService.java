package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.modules.personal.requests.OrgStructureRequest;
import kz.uco.tsadv.pojo.OrganizationRequestSaveModel;
import kz.uco.tsadv.pojo.PositionRequestSaveModel;

import java.util.UUID;

public interface OrgStructureRequestService {
    String NAME = "tsadv_OrgStructureRequestService";

    String getMergedOrgStructure(UUID requestId);

    OrgStructureRequest initialCreate();

    String saveOrganization(OrganizationRequestSaveModel organizationRequestSaveModel);

    String savePosition(PositionRequestSaveModel positionRequestSaveModel);

    String getGrades();

    void exclude(UUID requestId, UUID requestDetailId, UUID elementGroupId, int elementType);
}