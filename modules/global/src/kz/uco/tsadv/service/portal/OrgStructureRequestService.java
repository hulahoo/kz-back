package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.api.OrgStructureRequestChangeType;
import kz.uco.tsadv.api.OrgStructureRequestDisplayType;
import kz.uco.tsadv.modules.personal.requests.OrgStructureRequest;
import kz.uco.tsadv.pojo.OrgRequestSaveModel;
import kz.uco.tsadv.pojo.OrganizationRequestSaveModel;
import kz.uco.tsadv.pojo.PositionRequestSaveModel;
import kz.uco.tsadv.pojo.RequestTreeData;

import java.util.UUID;

public interface OrgStructureRequestService {
    String NAME = "tsadv_OrgStructureRequestService";

    String getMergedOrgStructure(UUID requestId);

    OrgStructureRequest initialCreate();

    OrgStructureRequest saveRequest(OrgRequestSaveModel orgRequestSaveModel);

    String saveOrganization(OrganizationRequestSaveModel organizationRequestSaveModel);

    String savePosition(PositionRequestSaveModel positionRequestSaveModel);

    String getGrades();

    void exclude(UUID requestId, RequestTreeData data);

    void exclude(UUID requestId, UUID requestDetailId, UUID elementGroupId, int elementType);

    String getMergedOrgStructure(UUID requestId, OrgStructureRequestChangeType changeTypeFilter);

    String getMergedOrgStructure(UUID requestId, OrgStructureRequestDisplayType displayFilter);

    boolean availableSalary();

    boolean hasPermitToCreate();
}