package kz.uco.tsadv.service;


import kz.uco.tsadv.global.entity.AssignmentAbsenceChartEntity;
import kz.uco.tsadv.global.entity.CompetenceChartEntity;
import kz.uco.tsadv.global.entity.SalaryChartEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface StatisticsService {
    String NAME = "tsadv_StatisticsService";

    List<CompetenceChartEntity> getCompetenceChart(UUID assignmentGroupId, String language);

    List<SalaryChartEntity> getSalaryChart(Double salary);

    List<AssignmentAbsenceChartEntity> getAssignmentAbsenceChart(UUID managerPositionGroupId, UUID assignmentGroupId, Date dateFrom, Date dateTo, UUID absenceTypeId);

}