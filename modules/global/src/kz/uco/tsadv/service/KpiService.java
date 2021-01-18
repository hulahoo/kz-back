package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.pojo.kpi.AssignedPerformancePlanListPojo;

import java.util.Date;
import java.util.List;

public interface KpiService {
    String NAME = "tsadv_KpiService";

    List<AssignedPerformancePlanListPojo> loadUsersPerformancePlans(Integer page, Integer pageSize);

    Long countUsersPerformancePlans();

    double calculationOfGZP(AssignmentGroupExt assignmentGroupExt, Date startDate, Date endDate);
}