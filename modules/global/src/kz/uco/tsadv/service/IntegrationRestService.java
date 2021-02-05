package kz.uco.tsadv.service;

import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.integration.jsonobject.JobDataJson;
import kz.uco.tsadv.modules.integration.jsonobject.OrganizationDataJson;
import kz.uco.tsadv.modules.integration.jsonobject.PersonDataJson;
import kz.uco.tsadv.modules.integration.jsonobject.PositionDataJson;

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
}