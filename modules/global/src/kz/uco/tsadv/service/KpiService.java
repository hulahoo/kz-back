package kz.uco.tsadv.service;

import kz.uco.tsadv.pojo.kpi.AssignedPerformancePlanListPojo;

import java.util.List;

public interface KpiService {
    String NAME = "tsadv_KpiService";

    List<AssignedPerformancePlanListPojo> loadUsersPerformancePlans(Integer page, Integer pageSize);

    Long countUsersPerformancePlans();
}