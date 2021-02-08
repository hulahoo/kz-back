package kz.uco.tsadv.service;

import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.integration.jsonobject.*;

public interface IntegrationRestService {
    String NAME = "tsadv_IntegrationRestService";

    BaseResult createOrUpdateOrganization(OrganizationDataJson organizationData);

    BaseResult deleteOrganization(OrganizationDataJson organizationData);

    BaseResult createOrUpdateJob(JobDataJson jobData);

    BaseResult deleteJob(JobDataJson jobData);

    BaseResult createOrUpdatePosition(PositionDataJson positionData);

    BaseResult deletePosition(PositionDataJson positionData);

    BaseResult createOrUpdatePerson(PersonDataJson personData);

    BaseResult deletePerson(PersonDataJson personData);

    BaseResult createOrUpdateOrganizationHierarchyElement(HierarchyElementDataJson hierarchyElementData);

    BaseResult deleteOrganizationHierarchyElement(HierarchyElementDataJson hierarchyElementData);
}