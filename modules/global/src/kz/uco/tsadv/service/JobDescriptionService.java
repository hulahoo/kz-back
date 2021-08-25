package kz.uco.tsadv.service;

import kz.uco.tsadv.entity.models.JobDescriptionRequestJson;

import java.util.List;

public interface JobDescriptionService {
    String NAME = "tsadv_JobDescriptionService";

    List<JobDescriptionRequestJson> getJobDescriptionRequests(String positionId);
}