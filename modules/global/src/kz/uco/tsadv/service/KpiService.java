package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.performance.model.Goal;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationGroupGoalLink;
import kz.uco.tsadv.pojo.kpi.AssignedPerformancePlanListPojo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface KpiService {
    String NAME = "tsadv_KpiService";

    List<AssignedPerformancePlanListPojo> loadUsersPerformancePlans(Integer page, Integer pageSize);

    Long countUsersPerformancePlans();

    double calculationOfGZP(AssignmentGroupExt assignmentGroupExt, Date startDate, Date endDate);

    BigDecimal calculationOfGzpWithAbsences(PersonGroupExt personGroupExt, Date startDate, Date endDate);

    BigDecimal getMaxBonus(PersonGroupExt personGroupExt, Date startDate, Date endDate);

    List kpiAssignedGoals(UUID appId);

    List<OrganizationGroupGoalLink> getHierarchyGoals(UUID organizationGroupId);
}