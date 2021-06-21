package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.pojo.GanttChartVacationScheduleData;
import kz.uco.tsadv.pojo.pagination.EntitiesPaginationResult;
import kz.uco.tsadv.pojo.pagination.PaginationPojo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface VacationScheduleRequestService {
    String NAME = "tsadv_VacationScheduleRequestService";

    EntitiesPaginationResult getChildVacationSchedule(PaginationPojo paginationPojo);

    List<GanttChartVacationScheduleData> ganttChart(Date startDate, Date endDate);

    List<GanttChartVacationScheduleData> ganttChart(UUID personGroupId, Date startDate, Date endDate);

}