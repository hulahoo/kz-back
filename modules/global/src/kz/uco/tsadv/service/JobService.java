package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;

import java.util.List;

public interface JobService {
    String NAME = "tsadv_JobService";

    boolean checkForExistingPositionsInSameDate(JobGroup jobGroup);

    List<PositionExt> getExistingPositionsInSameDate(JobGroup jobGroup, String view);
}